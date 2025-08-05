package TOTs.OOTDay.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 로그인 성공 시 JWT 토큰 DTO
@Getter
@AllArgsConstructor
public class MemberLoginResponseDTO {
    private String token; //JWT 토큰
}
