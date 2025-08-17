package TOTs.OOTDay.member.DTO;

import lombok.*;

//회원가입 요청 데이터
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberJoinDTO {
    private String name; // 이름
    private String phoneNumber; // 전화번호
    private String memberId; // 아이디
    private String password; // 비밀번호
    private String confirmPassword; // 비밀번호 재확인
    //private MemberEntity.Gender gender; // 성별 -> MALE/FEMALE
    //private boolean agree; //약관 동의 여부
}
