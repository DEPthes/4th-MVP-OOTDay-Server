package TOTs.OOTDay.member.Service;

import TOTs.OOTDay.member.Entity.RefreshTokenEntity;
import TOTs.OOTDay.member.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RememberMeService {
    private final RefreshTokenRepository refreshTokenRepository;

    // Refresh Token 생성
    public String issueRefreshToken(String memberId, int days) {
        String rawToken = UUID.randomUUID().toString(); // 난수로 구현
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(days); // 만료 시간

        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .memberId(memberId)
                .token(rawToken)
                .expiresAt(expiresAt)
                .build();
        refreshTokenRepository.save(entity);

        return rawToken;
    }

    public Optional<String> rotateIfValid(String refreshToken) {
        Optional<RefreshTokenEntity> opt = refreshTokenRepository.findByToken(refreshToken);
        if(opt.isEmpty()) {
            return Optional.empty();
        }

        RefreshTokenEntity saved = opt.get();
        if(saved.getExpiresAt().isBefore(LocalDateTime.now())) { // 토큰 만료 되었는지
            refreshTokenRepository.deleteByToken(refreshToken); // 만료된 토큰 정리
            return Optional.empty();
        }

        String newToken = UUID.randomUUID().toString(); // 새로운 토큰 생성
        saved.setToken(newToken);
        refreshTokenRepository.save(saved);

        return Optional.of(saved.getMemberId() + " : " + newToken);
    }

    // 단말기 1개 로그아웃
    public void revoke(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    // 모든 기기 로그아웃
    public void revokeAll(String memberId) {
        refreshTokenRepository.deleteAllByMemberId(memberId);
    }
}
