package TOTs.OOTDay.member;


import TOTs.OOTDay.member.DTO.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<UUID> join(@RequestBody MemberJoinDTO joinDTO) {
        UUID id = memberService.join(joinDTO); // 회원가입 후 UUID 받아옴.
        return ResponseEntity.ok(id);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDTO> login(@RequestBody MemberLoginDTO loginDTO) {
        String token = memberService.login(loginDTO); // JWT 토큰 발급

        return ResponseEntity.ok(new MemberLoginResponseDTO(token));
    }

    // 설문
    @PostMapping("/survey")
    public ResponseEntity<String> updateSurvey(@RequestHeader("Authorization") String token, @RequestBody SurveyDTO dto) {
        memberService.updateSurvey(token, dto);
        return ResponseEntity.ok("설문 정보가 저장되었습니다.");
    }

    // 회원탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody MemberWithdrawDTO dto, @RequestHeader("Authorization") String token) {
        memberService.withdraw(dto, token); // 회원 탈퇴
        return ResponseEntity.ok("회원 탈퇴가 완료되었어요.");

    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        return ResponseEntity.ok("로그아웃");
    }

    // 아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        return ResponseEntity.ok(memberService.findId(phoneNumber));
    }

    // 내 프로필
    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getProfile(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(memberService.getProfile(token));
    }

    // 프로필 -> 성별/퍼스널 컬러 업데이트
    @PatchMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestHeader("Authorization") String token, @RequestBody ProfileUpdateDTO dto){
        memberService.updateProfile(token, dto);
        return ResponseEntity.ok("프로필이 수정되었습니다.");
    }
}
