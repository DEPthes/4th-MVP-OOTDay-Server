package TOTs.OOTDay.sms;

import TOTs.OOTDay.member.Service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
@Tag(name = "SMS", description = "SMS 인증 관련 API")
public class SmsController {
    private final SmsService smsService;
    private final MemberService memberService;

    private final DefaultMessageService messageService;

    // 회원가입 시 인증번호 전송 요청
    @PostMapping("/send")
    @Operation(
            summary = "회원가입 인증번호 전송",
            description = "회원가입 시 휴대폰 번호로 인증번호를 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "전송 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성/전송 실패")
            }

    )
    public ResponseEntity<String> sendSignupCode(
            @Parameter(description = "전화번호(숫자 11자리)", example = "01012345678", required = true)
            @RequestParam String phoneNumber) {
        smsService.sendSignupVerificationCode(phoneNumber); // 인증번호 전송
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

    // 회원가입 시 인증번호 검증 요청
    @PostMapping("/verify")
    @Operation(
            summary = "회원가입 인증번호 검증",
            description = "전송된 인증번호가 일치하는지 검증합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "검증 결과(성공/실패)",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "유효성 실패")
            }

    )
    public ResponseEntity<String> verifySignupCode(
            @Parameter(description = "전화번호(숫자 11자리)", example = "01012345678", required = true)
            @RequestParam String phoneNumber,
            @Parameter(description = "인증번호(6자리)", example = "123456", required = true)
            @RequestParam String code) {
        boolean success = smsService.verifySignupCode(phoneNumber, code); // 코드 일치하는지 확인
        return ResponseEntity.ok(success ? "인증 성공" : "인증 실패");
    }

    // 아이디 찾기용 인증번호 전송
    @PostMapping("/send/find-id")
    @Operation(
            summary = "아이디 찾기용 인증번호 전송",
            description = "아이디 찾기에 사용할 인증번호를 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "전송 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성/전송 실패")
            }

    )
    public ResponseEntity<String> sendFindAccountCode(
            @Parameter(description = "전화번호(숫자 11자리)", example = "01012345678", required = true)
            @RequestParam String phoneNumber) {
        smsService.sendFindAccountVerificationCode(phoneNumber);
        return ResponseEntity.ok("아이디 찾기 인증번호가 전송되었습니다.");
    }

    // 아이디/비밀번호 찾기 인증번호 검증
    @PostMapping("/verify/find-account")
    @Operation(
            summary = "아이디/비밀번호 찾기 인증번호 검증",
            description = "아이디/비밀번호 찾기용 인증번호를 검증합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "검증 결과(성공/실패)",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "유효성 오류",
                            content = @Content(schema = @Schema(implementation = String.class)))
            }

    )
    public ResponseEntity<String> verifyFindAccountCode(
            @Parameter(description = "전화번호(숫자 11자리)", example = "01012345678", required = true)
            @RequestParam String phoneNumber,
            @Parameter(description = "인증번호(6자리)", example = "654321", required = true)
            @RequestParam String code) {
        boolean success = smsService.verifyFindAccountCode(phoneNumber, code);
        return ResponseEntity.ok(success ? "인증 성공" : "인증 실패");
    }

    // 비밀번호 재설정 시 휴대폰 인증 발송
    @PostMapping("/send/reset-password")
    @Operation(
            summary = "비밀번호 재설정용 인증번호 전송",
            description = "아이디/전화번호 일치 확인 후, 비밀번호 재설정에 필요한 인증번호를 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "전송 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성 오류/일치하지 않음",
                            content = @Content(schema = @Schema(implementation = String.class)))
            }

    )
    public ResponseEntity<String> sendResetPasswordCode(@RequestBody ResetPasswordSendCodeDTO dto) {
        memberService.sendResetPasswordCode(dto.getMemberId(), dto.getPhoneNumber());
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    // 비밀번호 재설정 시 인증 후 재설정
    @PostMapping("/reset-password")
    @Operation(
            summary = "비밀번호 재설정",
            description = "휴대폰 인증 완료 후 새 비밀번호로 재설정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "재설정 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성 오류/인증 미완료",
                            content = @Content(schema = @Schema(implementation = String.class)))
            }

    )
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO dto){
        memberService.resetPassword(dto);
        return ResponseEntity.ok("비밀번호가 재설정 되었습니다.");
    }
}
