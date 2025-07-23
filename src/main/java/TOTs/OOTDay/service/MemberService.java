package TOTs.OOTDay.service;

import TOTs.OOTDay.dto.MemberJoinDTO;
import TOTs.OOTDay.entity.MemberEntity;
import TOTs.OOTDay.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    public UUID join(MemberJoinDTO joinDTO) {

        //존재하는지 확인
        if(memberRepository.existsByMemberId(joinDTO.getMemberId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        //DTO -> Entity 변환
        MemberEntity memberEntity = MemberEntity.builder()
                .memberId(joinDTO.getMemberId())
                .password(passwordEncoder.encode(joinDTO.getPassword()))
                .phoneNumber(joinDTO.getPhoneNumber())
                .gender(joinDTO.getGender())
                .build();

        // DB 저장 후 UUID 받아오기
        MemberEntity savedId = memberRepository.save(memberEntity);
        return savedId.getId(); // UUID로 반환
    }

    // 이름 가능한지
    public void validateName(String name) {
        // 비어있는지
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 비어 있을 수 없어요.");
        }

        // 한글/영어 O , 숫자/특수문자 X
        if(!name.matches("^[가-힣a-zA-Z]{2,5}$")) {
            throw new IllegalArgumentException("숫자, 특수문자, 띄어쓰기는 사용할 수 없어요.");
        }
    }

    // 전화 번호 --> 아직 문자 인증은 X
    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("전화번호는 비어 있을 수 없어요.");
        }

        //프론트에서 82+를 받아오는 것인지 물어보기
        if (!phoneNumber.matches("^\\+82(10\\d{8})$")) {
            throw new IllegalArgumentException("전화번호는 +821012345678 형식이어야 해요.");
        }
    }
}
