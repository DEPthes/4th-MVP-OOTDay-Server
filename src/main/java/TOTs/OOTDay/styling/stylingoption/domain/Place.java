package TOTs.OOTDay.styling.stylingoption.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Place {

    @Id
    @GeneratedValue
    private Long id;


    private String placeName;

    public Place(String placeName) {
        this.placeName = placeName;
    }
}
