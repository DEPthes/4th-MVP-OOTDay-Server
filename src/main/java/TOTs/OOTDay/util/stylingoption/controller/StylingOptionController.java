package TOTs.OOTDay.util.stylingoption.controller;

import TOTs.OOTDay.util.stylingoption.domain.MoodDto;
import TOTs.OOTDay.util.stylingoption.domain.PlaceDto;
import TOTs.OOTDay.util.stylingoption.service.MoodService;
import TOTs.OOTDay.util.stylingoption.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/styling/keyword")
public class StylingOptionController {

    private final MoodService moodService;
    private final PlaceService placeService;

    @PostMapping("/mood")
    public void saveMood(String input) {
        moodService.save(input);
    }

    @PostMapping("/place")
    public void savePlace(String input) {
        placeService.save(input);
    }

    @GetMapping("/mood")
    public List<MoodDto> findAllMood() {
        return moodService.findAll();
    }

    @GetMapping("/place")
    public List<PlaceDto> findAllPlace() {
        return placeService.findAll();
    }

}
