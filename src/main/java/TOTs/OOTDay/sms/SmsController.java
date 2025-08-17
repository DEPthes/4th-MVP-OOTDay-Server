package TOTs.OOTDay.sms;

import TOTs.OOTDay.member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {
    private final SmsService smsService;
    private final MemberService memberService;

    private final DefaultMessageService messageService;

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

    // 아이디 찾기용 인증번호 전송
    @PostMapping("/send/find-id")
    public ResponseEntity<String> sendFindAccountCode(@RequestParam String phoneNumber) {
        smsService.sendFindAccountVerificationCode(phoneNumber);
        return ResponseEntity.ok("아이디 찾기 인증번호가 전송되었습니다.");
    }

    // 아이디/비밀번호 찾기 인증번호 검증
    @PostMapping("/verify/find-account")
    public ResponseEntity<String> verifyFindAccountCode(@RequestParam String phoneNumber, @RequestParam String code) {
        boolean success = smsService.verifyFindAccountCode(phoneNumber, code);
        return ResponseEntity.ok(success ? "인증 성공" : "인증 실패");
    }

    // 비밀번호 재설정 시 휴대폰 인증 발송
    @PostMapping("/send/reset-password")
    public ResponseEntity<String> sendResetPasswordCode(@RequestBody ResetPasswordSendCodeDTO dto) {
        memberService.sendResetPasswordCode(dto.getMemberId(), dto.getPhoneNumber());
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    // 비밀번호 재설정 시 인증 후 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO dto){
        memberService.resetPassword(dto);
        return ResponseEntity.ok("비밀번호가 재설정 되었습니다.");
    }
}
