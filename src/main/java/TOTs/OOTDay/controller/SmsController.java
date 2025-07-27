package TOTs.OOTDay.controller;

import TOTs.OOTDay.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {
    private final SmsService smsService;

    // 인증번호 전송 요청
    @PostMapping("/send")
    public ResponseEntity<String> sendCode(@RequestParam String phoneNumber) {
        smsService.sendVerificationCode(phoneNumber); // 인증번호 전송
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

    // 인증번호 검증 요청
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String phoneNumber, @RequestParam String code) {
        boolean success = smsService.verifyCode(phoneNumber, code); // 코드 일치하는지 확인
        return ResponseEntity.ok(success ? "인증 성공" : "인증 실패");
    }
}
