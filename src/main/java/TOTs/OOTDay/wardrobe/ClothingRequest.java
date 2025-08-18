package TOTs.OOTDay.wardrobe;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClothingRequest { //dto

    private UUID uuid;
    private String name;
    private ClothCategory category;
    private String mood;
    private String description;
    private String imageUrl;

    public ClothingRequest() {
    }
}
