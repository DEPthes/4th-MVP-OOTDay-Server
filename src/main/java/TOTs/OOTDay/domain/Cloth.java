package TOTs.OOTDay.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Entity
@Builder
@Getter
@AllArgsConstructor
public class Cloth {

    @Id
    @GeneratedValue
    @Column(name = "cloth_id")
    private Long id; //pk

    private UUID uuid; //옷 삭제를 위한 id(외부로 노출)

    @PrePersist
    public void generateUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }

    private String name; //사진 이름

    private String category; //상의와 하의등 카테고리 분류

    private String mood; //캐주얼 스트릿 등 무드

    private String description;//옷에 대한 간단한 설명

    public Cloth() {

    }
}
