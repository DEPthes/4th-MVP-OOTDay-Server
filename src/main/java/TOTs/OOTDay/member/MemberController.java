package TOTs.OOTDay.member;


import TOTs.OOTDay.config.CookieUtil;
import TOTs.OOTDay.config.JwtUtil;
import TOTs.OOTDay.member.DTO.*;
import TOTs.OOTDay.member.Service.MemberService;
import TOTs.OOTDay.member.Service.RememberMeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final RememberMeService rememberMeService;

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<UUID> join(@RequestBody MemberJoinDTO joinDTO) {
        UUID id = memberService.join(joinDTO); // 회원가입 후 UUID 받아옴.
        return ResponseEntity.ok(id);
    }
/*
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDTO> login(@RequestBody MemberLoginDTO loginDTO) {
        String token = memberService.login(loginDTO); // JWT 토큰 발급

        return ResponseEntity.ok(new MemberLoginResponseDTO(token));
    }
 */

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDTO> login(@RequestBody MemberLoginDTO loginDTO, HttpServletResponse response) {
        // 1) Access Token 발급
        String token = memberService.login(loginDTO);

        // 2) rememberMe가 true면 Refresh Token 발급 + HttpOnly 쿠키로 설정
        if(loginDTO.isRememberMe()) {
            String refresh = memberService.issueRefreshTokenIfNeeded(loginDTO.getMemberId(), true); // 14일 토큰
            if(refresh != null) {
                CookieUtil.addRefreshTokenCookie(response, refresh, 14 * 24 * 3600); // 14일 * 24시간 * 3600초
            }
        }

        // 3) 바디에 Token 반환
        return ResponseEntity.ok(new MemberLoginResponseDTO(token));
    }

    // Token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<MemberLoginResponseDTO> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if(refreshToken == null) {
            throw new IllegalArgumentException("유효한 Refresh Token이 없습니다.");
        }

        Optional<String> rotated = rememberMeService.rotateIfValid(refreshToken);
        if(rotated.isEmpty()) {
            CookieUtil.clearRefreshTokenCookie(response);
            throw new IllegalArgumentException("Refresh Token이 유효하지 않거나 만료되었습니다. 다시 로그인 해주세요.");
        }

        String[] parts = rotated.get().split(":", 2);
        String memberId = parts[0];
        String newRefresh = parts[1];

        String newAccess = JwtUtil.generateToken(memberId);

        CookieUtil.addRefreshTokenCookie(response, newRefresh, 14 * 24 * 3600);

        return ResponseEntity.ok(new MemberLoginResponseDTO(newAccess));
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
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if(refreshToken != null) {
            rememberMeService.revoke(refreshToken);
        }
        CookieUtil.clearRefreshTokenCookie(response);
        return ResponseEntity.ok("로그아웃 되었습니다.");
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

    // 쿠키에서 refresh_token 추출
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(c -> CookieUtil.REFRESH_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }
}
