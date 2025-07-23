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
        if (memberRepository.existsByMemberId(joinDTO.getMemberId())) {
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
}
