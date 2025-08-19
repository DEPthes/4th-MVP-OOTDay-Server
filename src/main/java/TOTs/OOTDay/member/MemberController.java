package TOTs.OOTDay.member;


import TOTs.OOTDay.config.CookieUtil;
import TOTs.OOTDay.config.JwtUtil;
import TOTs.OOTDay.member.DTO.*;
import TOTs.OOTDay.member.Service.MemberService;
import TOTs.OOTDay.member.Service.RememberMeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;
    private final RememberMeService rememberMeService;

    // 회원가입
    @PostMapping("/join")
    @Operation(
            summary = "회원가입",                                  // 요약
            description = "회원가입을 수행하고 생성된 회원 UUID를 반환합니다.", // 상세설명
            responses = {
                    @ApiResponse(responseCode = "200", description = "가입 성공",
                            content = @Content(schema = @Schema(implementation = UUID.class))),
                    @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
                            content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    public ResponseEntity<UUID> join(
            @io.swagger.v3.oas.annotations.parameters.RequestBody( // 요청 바디 설명
            description = "회원가입 요청 바디",
            required = true,
            content = @Content(schema = @Schema(implementation = MemberJoinDTO.class))
            )
            @RequestBody MemberJoinDTO joinDTO) {
        UUID id = memberService.join(joinDTO); // 회원가입 후 UUID 받아옴.
        return ResponseEntity.ok(id);
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "로그인 후 Access 토큰(JWT)을 반환합니다. rememberMe=true면 Refresh 토큰을 HttpOnly 쿠키로 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = MemberLoginResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "아이디/비밀번호 오류",
                            content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    public ResponseEntity<MemberLoginResponseDTO> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MemberLoginDTO.class))
            )
            @RequestBody MemberLoginDTO loginDTO, HttpServletResponse response) {
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
    @Operation(
            summary = "Access 토큰 재발급",
            description = "HttpOnly 쿠키의 Refresh 토큰을 검증하여 새 Access 토큰을 발급합니다. " +
                    "성공 시 Refresh 토큰은 회전되어 새 쿠키로 교체됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "재발급 성공",
                            content = @Content(schema = @Schema(implementation = MemberLoginResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "요청 오류",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은/만료된 Refresh 토큰",
                            content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
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
    @Operation(
            summary = "설문 저장",
            description = "사용자의 성별/퍼스널 컬러 설문을 저장합니다.",
            security = @SecurityRequirement(name = "bearerAuth"), // JWT 필요
            responses = {
                    @ApiResponse(responseCode = "200", description = "저장 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "400", description = "유효성 실패")
            }
    )
    public ResponseEntity<String> updateSurvey(
            @Parameter(description = "Authorization 헤더 (Bearer {JWT})", required = true, example = "Bearer eyJhbGciOi...")
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "설문 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SurveyDTO.class))

            )
            @RequestBody SurveyDTO dto) {
        memberService.updateSurvey(token, dto);
        return ResponseEntity.ok("설문 정보가 저장되었습니다.");
    }

    // 회원탈퇴
    @DeleteMapping("/withdraw")
    @Operation(
            summary = "회원탈퇴",
            description = "동의 여부 확인 후 회원 정보를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "탈퇴 완료"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "400", description = "유효성 실패")
            }

    )
    public ResponseEntity<String> withdraw(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원탈퇴 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MemberWithdrawDTO.class))

            )
            @RequestBody MemberWithdrawDTO dto,
            @Parameter(description = "Authorization 헤더 (Bearer {JWT})", required = true)
            @RequestHeader("Authorization") String token) {
        memberService.withdraw(dto, token); // 회원 탈퇴
        return ResponseEntity.ok("회원 탈퇴가 완료되었어요.");

    }

    // 로그아웃
    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "현재 기기의 Refresh 토큰을 폐기하고 쿠키를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 완료")
            }

    )
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
    @Operation(
            summary = "아이디 찾기",
            description = "휴대폰 인증 완료 후 전화번호로 아이디를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "인증 미완료/미가입",
                            content = @Content(schema = @Schema(implementation = String.class)))
            }

    )
    public ResponseEntity<String> findId(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "요청 바디 예: {\"phoneNumber\":\"01012345678\"}",
                    required = true

            )
            @RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        return ResponseEntity.ok(memberService.findId(phoneNumber));
    }

    // 내 프로필
    @GetMapping("/profile")
    @Operation(
            summary = "내 프로필 조회",
            description = "로그인 사용자의 프로필을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = ProfileDTO.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패")
            }

    )
    public ResponseEntity<ProfileDTO> getProfile(
            @Parameter(description = "Authorization 헤더 (Bearer {JWT})", required = true)
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(memberService.getProfile(token));
    }

    // 프로필 -> 성별/퍼스널 컬러 업데이트
    @PatchMapping("/update-profile")
    @Operation(
            summary = "프로필 수정(PATCH)",
            description = "성별/퍼스널 컬러 등 일부 필드를 부분 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "400", description = "유효성 실패")
            }

    )
    public ResponseEntity<String> updateProfile(
            @Parameter(description = "Authorization 헤더 (Bearer {JWT})", required = true)
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "부분 수정 바디(둘 중 하나만 보내도 됨)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProfileUpdateDTO.class))
            )
            @RequestBody ProfileUpdateDTO dto){
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
