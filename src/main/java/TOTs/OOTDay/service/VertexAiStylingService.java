package TOTs.OOTDay.service;

import TOTs.OOTDay.domain.ClothingRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VertexAiStylingService {
    public List<List<ClothingRequest>> styleCloth(List<ClothingRequest> imageList) throws IOException {

        try (VertexAI vertexAI = new VertexAI("api-test-466115", "us-central1")) {
            GenerativeModel model = new GenerativeModel("gemini-2.0-flash-001", vertexAI);

            List<Part> parts = new ArrayList<>();

            parts.add(Part.newBuilder()
                    .setText(
                            "아래의 옷 사진들로 만들 수 있는 3가지 다른 코디를 추천해줘. " +
                                    "각 코디는 옷의 uuid와 name을 함께 고려해서 구성해줘. " +
                                    "결과는 다음 형식의 JSON 배열로 줘:\n" +
                                    "[[\"a1b2c3_uuid\", \"b4d5e6_uuid\"], [\"...\", \"...\"]]"
                    )
                    .build());

            for (ClothingRequest request : imageList) {
                parts.add(Part.newBuilder()
                        .setText("uuid: " + request.getUuid() + ", name: " + request.getName())
                        .build());

                try (InputStream in = (new URI(request.getImageUrl())).toURL().openStream()) {
                    byte[] bytes = in.readAllBytes();
                    parts.add(Part.newBuilder()
                            .setInlineData(Blob.newBuilder()
                                    .setData(ByteString.copyFrom(bytes))
                                    .setMimeType("image/jpeg").build())
                            .build());

                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            Content content = Content.newBuilder()
                    .setRole("user")
                    .addAllParts(parts)
                    .build();

            GenerateContentResponse response = model.generateContent(content);

            System.out.println("====response====\n" + response);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonText = response.getCandidates(0).getContent().getParts(0).getText(); // ✅ 응답에서 텍스트만 추출
            String json = extractJsonFromGeminiResponse(jsonText);

            List<List<String>> parsedOutfits;
            try {
                parsedOutfits = objectMapper.readValue(json, new TypeReference<>() {});
            } catch (Exception e) {
                throw new RuntimeException("AI 응답 JSON 파싱 실패: " + json, e);
            }

            System.out.println("===========\n" + parsedOutfits.toString());
            // ✅ ClothingRequest 매핑
            List<List<ClothingRequest>> result = new ArrayList<>();

            for (List<String> parsedOutfit : parsedOutfits) {
                List<ClothingRequest> matched = new ArrayList<>();
                for (String uuid : parsedOutfit) {
                    imageList.stream()
                            .filter(cloth -> cloth.getUuid().toString().equals(uuid))
                            .findFirst()
                            .ifPresent(matched::add);
                }
                result.add(matched);
            }

            return result;

        }
    }


    private String extractJsonFromGeminiResponse(String response) {//gemini 응답값에 백틱이 발생해 json을 찾지 못하는 오류로 인하여 백틱 제거 메서드 추가
        int start = response.indexOf("```json");
        int end = response.lastIndexOf("```");

        if (start != -1 && end != -1 && end > start) {
            return response.substring(start + 7, end).trim();
        }

        if (response.trim().startsWith("{")) {
            return response.trim();
        }

        throw new RuntimeException("openAi 응답에서 JSON을 찾을 수 없습니다:\n" + response);
    }
}
