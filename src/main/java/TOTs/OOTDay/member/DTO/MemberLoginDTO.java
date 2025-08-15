package TOTs.OOTDay.member.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginDTO {
    private String memberId; // 아이디
    private String password; // 비밀번호
    private boolean rememberMe; // 로그인 상태 유지 여부
}
