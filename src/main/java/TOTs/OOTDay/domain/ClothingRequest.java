package TOTs.OOTDay.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClothingRequest { //dto

    private UUID uuid;
    private String name;
    private String category;
    private String mood;
    private String description;

    public ClothingRequest() {
    }
}
