package TOTs.OOTDay.service;

import TOTs.OOTDay.domain.GeminiClothingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final VertexAiGeminiChatModel model;

    public GeminiClothingRequest analyzeCloth(MultipartFile imageFile) { // gemini 한테 사진 보내기
        try {
            byte[] imageBytes = imageFile.getBytes();

            Media image = Media.builder().data(imageBytes).mimeType(MimeTypeUtils.IMAGE_PNG).build();

            UserMessage userMessage = UserMessage.builder().media(image).text("이 옷 사진을 분석해서 아래와 같은 JSON 응답해줘:\n" +
                    "{ \"name\": \"슈프림 반바지\", \"category\": \"상의\", \"mood\": \"캐주얼\", \"description\": \"설명\" }").build();

            String jsonFromGeminiResponse = getJsonFromGeminiResponse(userMessage);//gemini 한테 사진 보낸 후 응답 json 받기

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonFromGeminiResponse, GeminiClothingRequest.class);

        } catch (IOException e) {
            throw new RuntimeException("파일 처리 또는 JSON 파싱 오류", e);
        } catch (Exception e) {
            throw new RuntimeException("Gemini 요청 실패", e);
        }
    }

    private String getJsonFromGeminiResponse(UserMessage userMessage) {
        Prompt prompt = new Prompt(userMessage);

        List<Generation> responses = model.call(prompt).getResults(); // gemini 한테 사진과 텍스트 보냄
        String content = responses.getFirst().getOutput().getText(); //결과 꺼내기

        return extractJsonFromGeminiResponse(content);// 백틱을 제거한 json 응답값
    }


    private String extractJsonFromGeminiResponse(String response) {//gemini 응답값에 백틱이 발생해 json을 찾지 못하는 오류로 인하여 백틱 제거 메서드 추가
        int start = response.indexOf("```json");
        int end = response.lastIndexOf("```");

        if (start != -1 && end != -1 && end > start) {
            return response.substring(start + 7, end).trim();
        }

        // fallback: 그냥 { 로 시작하면 그대로 반환
        if (response.trim().startsWith("{")) {
            return response.trim();
        }

        throw new RuntimeException("Gemini 응답에서 JSON을 찾을 수 없습니다:\n" + response);
    }

}
