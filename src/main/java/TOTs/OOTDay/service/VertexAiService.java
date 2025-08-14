package TOTs.OOTDay.service;

import TOTs.OOTDay.domain.ClothingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Blob;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VertexAiService {

    public ClothingRequest analyzeCloth(MultipartFile imageFile) throws IOException {
        try (VertexAI vertexAI = new VertexAI("api-test-466115", "us-central1")) {
            GenerativeModel model =  new GenerativeModel("gemini-2.0-flash-001", vertexAI);

            List<Part> parts = new ArrayList<>();

            Part part = Part.newBuilder()
                    .setInlineData(Blob.newBuilder()
                            .setData(ByteString.copyFrom(imageFile.getBytes()))
                            .setMimeType(imageFile.getContentType())
                            .build())
                    .build();

            Part text = Part.newBuilder().setText(
                    "이 옷 사진을 분석해서 아래와 같은 JSON 형식으로 응답해줘.\n" +
                            "{ \"name\": \"슈프림 반바지\", \"category\": \"상의\", \"mood\": \"캐주얼\", \"description\": \"설명\" }\n\n" +
                            "조건:\n" +
                            "1. category 값은 반드시 다음 중 하나여야 한다:\n" +
                            "   - 상의\n" +
                            "   - 하의\n" +
                            "   - 드레스\n" +
                            "   - 패션소품\n" +
                            "   - 신발\n" +
                            "   - 악세사리\n" +
                            "2. 위 목록에 없는 category는 절대 사용하지 말 것.\n" +
                            "3. category 값은 반드시 위에 적힌 형태와 철자를 정확히 맞출 것.\n" +
                            "4. JSON 외 다른 텍스트는 포함하지 말고, 반드시 JSON만 반환할 것."
            ).build();

            parts.add(part);
            parts.add(text);

            Content content = Content.newBuilder()
                    .setRole("user")
                    .addAllParts(parts)
                    .build();

            GenerateContentResponse response = model.generateContent(content);
            String jsonText = response.getCandidates(0).getContent().getParts(0).getText();

            String json = extractJsonFromGeminiResponse(jsonText);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, ClothingRequest.class);

        } catch (IOException e) {
            throw new RuntimeException("파일 처리 또는 JSON 파싱 오류", e);
        } catch (Exception e) {
            throw new RuntimeException("OpenAi 요청 실패", e);
        }
    }

    private String extractJsonFromGeminiResponse(String response) {//응답값에 백틱이 발생해 json을 찾지 못하는 오류로 인하여 백틱 제거 메서드 추가
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
