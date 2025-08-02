package TOTs.OOTDay.repository;

import TOTs.OOTDay.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository  extends JpaRepository<MemberEntity, UUID> {

    // memberId로 회원 조회
    Optional<MemberEntity> findByMemberId(String memberId);

    // memberId 중복 확인
    boolean existsByMemberId(String memberId);

    // phoneNumber 조회
    Optional<MemberEntity> findByPhoneNumber(String phoneNumber);
}