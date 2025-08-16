package TOTs.OOTDay.styling;

import TOTs.OOTDay.styling.stylingoption.domain.MoodDto;
import TOTs.OOTDay.styling.stylingoption.domain.PlaceDto;
import TOTs.OOTDay.wardrobe.ClothingRequest;
import lombok.Getter;

import java.util.List;

@Getter
public class StylingRequestDto {

    private List<ClothingRequest> imageList;
    private List<MoodDto> mood;
    private PlaceDto place;
}
