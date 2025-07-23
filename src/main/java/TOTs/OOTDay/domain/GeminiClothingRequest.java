package TOTs.OOTDay.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GeminiClothingRequest { //dto
    private String name;
    private String category;
    private String mood;
    private String description;

    public GeminiClothingRequest() {
    }
}
