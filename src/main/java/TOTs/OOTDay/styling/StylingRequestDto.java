package TOTs.OOTDay.styling;

import TOTs.OOTDay.styling.stylingoption.domain.MoodDto;
import TOTs.OOTDay.styling.stylingoption.domain.PlaceDto;
import TOTs.OOTDay.wardrobe.ClothingRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StylingRequestDto {

    private List<ClothingRequest> imageList;
    private List<MoodDto> mood;
    private PlaceDto place;
}
