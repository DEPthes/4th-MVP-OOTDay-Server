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

    // 회원가입 시 인증번호 전송 요청
    @PostMapping("/send")
    public ResponseEntity<String> sendSignupCode(@RequestParam String phoneNumber) {
        smsService.sendSignupVerificationCode(phoneNumber); // 인증번호 전송
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

    // 회원가입 시 인증번호 검증 요청
    @PostMapping("/verify")
    public ResponseEntity<String> verifySignupCode(@RequestParam String phoneNumber, @RequestParam String code) {
        boolean success = smsService.verifySignupCode(phoneNumber, code); // 코드 일치하는지 확인
        return ResponseEntity.ok(success ? "인증 성공" : "인증 실패");
    }

    // 아이디/비밀번호 찾기용 인증번호 전송
    @PostMapping("/send/find-account")
    public ResponseEntity<String> sendFindAccountCode(@RequestParam String phoneNumber) {
        smsService.sendFindAccountVerificationCode(phoneNumber);
        return ResponseEntity.ok("아이디/비밀번호 찾기 인증번호가 전송되었습니다.");
    }

    // 아이디/비밀번호 찾기용 인증번호 검증
    @PostMapping("/verify/find-account")
    public ResponseEntity<String> verifyFindAccountCode(@RequestParam String phoneNumber, @RequestParam String code) {
        boolean success = smsService.verifyFindAccountCode(phoneNumber, code);
        return ResponseEntity.ok(success ? "인증 성공" : "인증 실패");
    }
}
