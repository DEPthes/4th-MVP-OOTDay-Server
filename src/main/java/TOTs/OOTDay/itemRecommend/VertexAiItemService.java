package TOTs.OOTDay.itemRecommend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Blob;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class VertexAiItemService {

    @Value("${naver.client-id}")
    private String naverClientId;

    @Value("${naver.client-secret}")
    private String naverClientSecret;

    public ItemDto itemRecommend(MultipartFile image) throws IOException {

        VertexAiDto readValue;

        //vertexAi 한테 코디 이미지 전송 후 추천 아이템 json 으로 반환
        try (VertexAI vertexAI = new VertexAI("api-test-466115", "us-central1")) {
            GenerativeModel model = new GenerativeModel("gemini-2.0-flash-001", vertexAI);

            List<Part> parts = new ArrayList<>();

            parts.add(Part.newBuilder()
                    .setText(
                            """
                                    내가 제공하는 착장 이미지를 보고 이 착장에 어울리만한 악세사리 정보를 JSON으로 반환해줘
                                    반환 JSON 예시: {"category":"신발", "brand":"나이키", "color":"화이트","style":"캐주얼"}
                                    
                                    조건:
                                    1.하나의 상품만 반환해야해
                                    2.JSON 외 다른 텍스트는 포함하지 말고, 반드시 JSON만 반환할 것.
                                    """
                    ).build());


            parts.add(Part.newBuilder()
                    .setInlineData(Blob.newBuilder()
                            .setMimeType("image/jpeg")
                            .setData(ByteString.copyFrom(image.getBytes())))
                    .build()
            );

            Content content = Content.newBuilder()
                    .setRole("user")
                    .addAllParts(parts)
                    .build();

            GenerateContentResponse response = model.generateContent(content);//vertexAi 한테 전송 후 반환값 받기
            String text = response.getCandidates(0).getContent().getParts(0).getText();
            String json = extractJsonFromGeminiResponse(text);
            readValue = new ObjectMapper().readValue(json, VertexAiDto.class);
        }

        return naverApi(readValue);
    }

    //네이버 api를 사용하여 추천 아이템을 네이버 쇼핑에서 검색후 값 반환
    private ItemDto naverApi(VertexAiDto vertexAiDto) {

        String text = vertexAiDto.getCategory() + " " + vertexAiDto.getBrand() + " " + vertexAiDto.getColor() + " " + vertexAiDto.getStyle();

        ShoppingRequest request = new ShoppingRequest();
        request.setQuery(text);
        URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com/v1/search/shop")
                .queryParams(request.map())
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", naverClientId);
        headers.set("X-Naver-Client-Secret", naverClientSecret);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        //api에 요청 보내기 & 값 반환
        ResponseEntity<ShoppingResponse> entity = new RestTemplate().exchange(uri, HttpMethod.GET, httpEntity, ShoppingResponse.class);
        ShoppingResponse body = entity.getBody();

        List<ShoppingResponse.ShoppingItem> items = body.getItems();
        ShoppingResponse.ShoppingItem item = items.getFirst();
        return new ItemDto(item.getImage(), item.getLink());
    }

    private String extractJsonFromGeminiResponse(String response) {// 백틱 제거
        int start = response.indexOf("```json");
        int end = response.lastIndexOf("```");

        if (start != -1 && end != -1 && end > start) {
            return response.substring(start + 7, end).trim();
        }

        if (response.trim().startsWith("{")) {
            return response.trim();
        }

        throw new RuntimeException("vertexAi 응답에서 JSON을 찾을 수 없습니다:\n" + response);
    }

}
