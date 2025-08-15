package TOTs.OOTDay.member.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid") //UUID
    private UUID id;

    @Column
    private String memberId; // 사용자 아이디

    @Column(nullable = false, unique = true, length = 255)
    private String token; // Refresh Token

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 만료 시간

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 시각

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
