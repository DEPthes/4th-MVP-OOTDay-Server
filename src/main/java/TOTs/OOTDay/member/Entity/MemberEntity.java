package TOTs.OOTDay.member.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

// UserEntity(노션) -> MemberEntity(erd 다이어그램)
@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {
    @Id // 기본키
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid") // UUID
    private UUID id; // UUID

    @Column(nullable = false)
    private String name; // 이름

    @Column(name = "member_id", nullable = false, unique = true)
    private String memberId; // 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    /*
    @Column(name = "agree")
    private boolean agree; // 약관 동의 여부
     */

    /*
    @Column(length = 255)
    private String nickname; // 닉네임
    */

    @Column(length = 255)
    private String phoneNumber; // 전화번호

    @Enumerated(EnumType.STRING)
    private Gender gender; // 성별

    @Enumerated(EnumType.STRING) // 퍼스널 컬러 추가
    private PersonalColor personalColor;

    @Column(name = "create_at")
    private LocalDateTime createdAt; // 생성 시간

    @Column(name = "update_at")
    private LocalDateTime updatedAt; // 수정 시간

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum PersonalColor {
        SPRING, SUMMER, AUTUMN, WINTER
    }
}