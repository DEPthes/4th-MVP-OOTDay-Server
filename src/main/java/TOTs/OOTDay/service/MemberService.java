package TOTs.OOTDay.service;

import TOTs.OOTDay.config.JwtUtil;
import TOTs.OOTDay.dto.MemberJoinDTO;
import TOTs.OOTDay.dto.MemberLoginDTO;
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
    public UUID join(MemberJoinDTO dto) {
        // 이름 유효성 검사 (한글 또는 영어 2~5자, 숫자/공백/특수문자 불가)
        if (!dto.getName().matches("^[a-zA-Z가-힣]{2,5}$")) {
            throw new IllegalArgumentException("이름은 한글 또는 영어만 가능하며, 2~5자여야 해요.");
        }

        // 전화번호 유효성 검사 (숫자만 10자리 -> 82+(프런트) 1012345678)
        if (!dto.getPhoneNumber().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("전화번호는 82+ 1012345678이어야 해요.");
        }

        // 아이디 유효성 검사 (영어, 숫자 조합, 5~10자)
        if (!dto.getMemberId().matches("^[a-zA-Z0-9]{5,10}$")) {
            throw new IllegalArgumentException("아이디는 영문과 숫자를 조합한 5~10자여야 해요.");
        }

        if (memberRepository.existsByMemberId(dto.getMemberId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 비밀번호 유효성 검사 (8~10자, 영어/숫자/특수문자 포함)
        if (!dto.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,10}$")) {
            throw new IllegalArgumentException("비밀번호는 8~10자이며 영어, 숫자, 특수문자를 모두 포함해야 해요.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (!dto.isAgree()) {
            throw new IllegalArgumentException("이용약관에 동의해야 합니다.");
        }

        MemberEntity entity = MemberEntity.builder()
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .memberId(dto.getMemberId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .agree(dto.isAgree())
                .build();

        return memberRepository.save(entity).getId();
    }

    //로그인 구현
     public String login(MemberLoginDTO loginDTO) {
        //memberId로 사용자 조희
        MemberEntity member = memberRepository.findByMemberId(loginDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 비밀번호 확인
        if(!passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // 로그인 성공 -> JWT 토큰 생성 후 반환
        return JwtUtil.generateToken(member.getMemberId());
    }
}
