package TOTs.OOTDay.service;

import TOTs.OOTDay.domain.ClothingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenAiStylingService {
    public List<List<ClothingRequest>> styleCloth(List<ClothingRequest> imageList) throws IOException {
        Client client = Client.builder()
                .apiKey("AIzaSyCo-mvbNufP7PjEYFWSFbk8QqFAdjkLc4g")
                .build();

        List<Part> parts = new ArrayList<>();
        parts.add(Part.fromText(
                "아래의 옷 사진들로 만들 수 있는 3가지 코디를 추천해줘. " +
                        "각 코디는 옷 이름(name)을 기준으로 구성해줘. " +
                        "다음 형식으로 텍스트를 JSON 배열로 출력해줘: " +
                        "[ [\"셔츠\", \"청바지\"], [\"티셔츠\", \"반바지\"], [\"니트\", \"슬랙스\"] ]"
        ));

        for (ClothingRequest request : imageList) {
            parts.add(Part.fromUri(request.getImageUrl(), "image/jpeg"));
        }

        Content content = Content.builder().parts(parts).build();
        //텍스트 생성
        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.7f)
                .build();
        System.out.println(parts);
        GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash-001", content, config);
        String jsonText = response.text();

        List<List<String>> parsedCody;
        try {
            parsedCody = new ObjectMapper().readValue(jsonText, List.class);
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 JSON 파싱 실패: " + jsonText, e);
        }
        List<List<ClothingRequest>> result = new ArrayList<>();
        for (List<String> outfitNames : parsedCody) {
            List<ClothingRequest> matched = imageList.stream()
                    .filter(c -> outfitNames.contains(c.getName()))
                    .collect(Collectors.toList());
            result.add(matched);
        }

        return result;

    }

}
