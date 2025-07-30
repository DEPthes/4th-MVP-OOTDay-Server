package TOTs.OOTDay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${solapi.apiKey}")
    private String apiKey;

    @Value("${solapi.apiSecret}")
    private String apiSecret;

    @Value("${solapi.sender}")
    private String sender;

    private final Map<String, String> verificationMap = new ConcurrentHashMap<>(); // 인증번호 임시 저장
    private final Map<String, Boolean> verifiedPhoneMap = new ConcurrentHashMap<>(); // 인증 완료 전화번호 저장
    private final RestTemplate restTemplate = new RestTemplate();

    // 인증번호 생성
    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6자리
    }

    // 메시지 전송
    public void sendVerificationCode(String phoneNumber) {
        String code = generateCode();
        verificationMap.put(phoneNumber, code); // 코드 저장

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 요청 바디
            Map<String, Object> body = Map.of(
                    "messages", List.of(Map.of(
                            "to", phoneNumber,      // 수신자
                            "from", sender,         // 발신자
                            "text", "[OOTDay] 인증번호: " + code
                    ))
            );

            ObjectMapper mapper = new ObjectMapper();
            System.out.println("[DEBUG] JSON Body: " + mapper.writeValueAsString(body));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.exchange(
                    URI.create("https://api.solapi.com/messages/v4/send"),
                    HttpMethod.POST,
                    request,
                    String.class
            );

        } catch (Exception e) {
            throw new RuntimeException("문자 전송 실패", e);
        }
    }

    // 코드 검증
    public boolean verifyCode(String phoneNumber, String inputCode) {
        String savedCode = verificationMap.get(phoneNumber);
        boolean isCorrect = inputCode.equals(savedCode);
        if (isCorrect) {
            verifiedPhoneMap.put(phoneNumber, true);
        }
        return isCorrect;
    }

    // 회원가입 시 인증 여부 확인용 메서드
    public boolean isVerified(String phoneNumber) {
        return verifiedPhoneMap.getOrDefault(phoneNumber, false);
    }

    // 회원가입 완료 후 인증상태 및 코드 삭제 (중복 가입/보안)
    public void clearVerified(String phoneNumber) {
        verifiedPhoneMap.remove(phoneNumber);
        verificationMap.remove(phoneNumber);
    }
}