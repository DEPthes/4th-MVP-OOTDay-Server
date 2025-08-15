package TOTs.OOTDay.member.Repository;

import TOTs.OOTDay.member.Entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByToken(String token); // 토큰으로 조회
    void deleteByToken(String token); // 특정 토큰 삭제
    void deleteAllByMemberId(String memberId); // 사용자 토큰 전체 삭제
}
