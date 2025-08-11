package TOTs.OOTDay.util.stylingoption.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Mood {

    @Id
    @GeneratedValue
    private Long id;

    private String moodName;


    public Mood(String mood) {
        moodName = mood;
    }

}
