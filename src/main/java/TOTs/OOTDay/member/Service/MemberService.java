package TOTs.OOTDay.member.Service;

import TOTs.OOTDay.config.JwtUtil;
import TOTs.OOTDay.member.DTO.*;
import TOTs.OOTDay.member.Entity.MemberEntity;
import TOTs.OOTDay.member.Repository.MemberRepository;
import TOTs.OOTDay.sms.SmsService;
import TOTs.OOTDay.sms.ResetPasswordRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;
    private final RememberMeService rememberMeService;

    //회원가입
    public UUID join(MemberJoinDTO dto) {
        // 이름 유효성 검사 (한글 또는 영어 2~5자, 숫자/공백/특수문자 불가)
        if (!dto.getName().matches("^[a-zA-Z가-힣]{2,5}$")) {
            throw new IllegalArgumentException("이름은 한글 또는 영어만 가능하며, 2~5자여야 해요.");
        }

        // 전화번호 유효성 검사 (숫자만 11자리 -> 01012345678로 수정)
        if (!dto.getPhoneNumber().matches("^\\d{11}$")) {
            throw new IllegalArgumentException("전화번호는 01012345678같은 형식이어야 해요.");
        }

        if (!smsService.isSignupVerified(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("휴대폰 번호 인증이 필요합니다.");
        }

        // 아이디 유효성 검사 (영어, 숫자 조합, 5~10자)
        if (!dto.getMemberId().matches("^[a-zA-Z0-9]{5,20}$")) {
            throw new IllegalArgumentException("아이디는 영문과 숫자를 조합한 5~20자여야 해요.");
        }

        if (memberRepository.existsByMemberId(dto.getMemberId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 비밀번호 유효성 검사 (8~10자, 영어/숫자/특수문자 포함)
        if (!dto.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$")) {
            throw new IllegalArgumentException("비밀번호는 8~16자이며 영어, 숫자, 특수문자를 모두 포함해야 해요.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        /*
        if (!dto.isAgree()) {
            throw new IllegalArgumentException("이용약관에 동의해야 합니다.");
        }
         */

        MemberEntity entity = MemberEntity.builder()
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .memberId(dto.getMemberId())
                .password(passwordEncoder.encode(dto.getPassword()))
                //.agree(dto.isAgree())
                .build();

        smsService.clearSignupVerification(dto.getPhoneNumber());

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

    // Refresh Token 발급
    public String issueRefreshTokenIfNeeded(String memberId, boolean rememberMe) {
        if(!rememberMe) {
            return null;
        }

        return rememberMeService.issueRefreshToken(memberId, 14);
    }

    // 설문
    public void updateSurvey(String token, SurveyDTO dto) {
        String memberId = JwtUtil.getMemberIdFromToken(token);
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        member.setGender(MemberEntity.Gender.valueOf(dto.getGender()));
        member.setPersonalColor(MemberEntity.PersonalColor.valueOf(dto.getPersonalColor()));
        memberRepository.save(member);
    }

    // 회원탈퇴
    public void withdraw(MemberWithdrawDTO dto, String token) {
        String memberId = JwtUtil.getMemberIdFromToken(token);

        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        /*
        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        */

        if(!dto.isAgree()) {
            throw new IllegalArgumentException("유의사항에 동의해야 탈퇴가 가능합니다.");
        }

        memberRepository.delete(member); // DB에서 회원 삭제

    }

    // 아이디 찾기
    public String findId(String phoneNumber) {
        if(!smsService.isFindAccountVerified(phoneNumber)) {
            throw new IllegalArgumentException("휴대폰 인증이 필요합니다.");
        }

        MemberEntity member = memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("등록된 회원이 없습니다."));

        smsService.clearFindAccountVerification(phoneNumber);
        return member.getMemberId();
    }

    // 아이디와 휴대폰 번호가 일치하는지
    public void sendResetPasswordCode(String memberId, String phoneNumber) {
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if(!member.getPhoneNumber().equals(phoneNumber)) {
            throw new IllegalArgumentException("아이디와 휴대폰 번호가 일치하지 않습니다.");
        }

        // 인증번호 전송
        smsService.sendFindAccountVerificationCode(phoneNumber);
    }

    // 휴대폰 번호 인증 후 비밀번호 재설정
    public void resetPassword(ResetPasswordRequestDTO dto) {
        if(!smsService.isFindAccountVerified(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("휴대폰 인증이 필요합니다.");
        }

        MemberEntity member = memberRepository.findByMemberId(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if(!member.getPhoneNumber().equals(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("아이디와 휴대폰 번호가 일치하지 않습니다.");
        }

        if(!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if(passwordEncoder.matches(dto.getNewPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이전 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.");
        }

        // 재설정 비밀번호 암호화
        member.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        memberRepository.save(member);

        // 인증 정보 삭제
        smsService.clearFindAccountVerification(dto.getPhoneNumber());
    }

    // 프로필 업데이트
    public ProfileDTO getProfile(String token) {
        String memberId = JwtUtil.getMemberIdFromToken(token);
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        ProfileDTO dto = new ProfileDTO();
        dto.setName(member.getName());
        dto.setMemberId(member.getMemberId());
        dto.setGender(member.getGender() != null ? member.getGender().name() : null);
        dto.setPersonalColor(member.getPersonalColor() != null ? member.getPersonalColor().name() : null);
        return dto;
    }

    // 프로필 업데이트
    public void updateProfile(String token, ProfileUpdateDTO dto) {
        String memberId = JwtUtil.getMemberIdFromToken(token);
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if(dto.getGender() != null) {
            member.setGender(MemberEntity.Gender.valueOf(dto.getGender()));
        }
        if(dto.getPersonalColor() != null) {
            member.setPersonalColor(MemberEntity.PersonalColor.valueOf(dto.getPersonalColor()));
        }

        memberRepository.save(member);
    }
}
