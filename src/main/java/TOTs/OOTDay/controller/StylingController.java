package TOTs.OOTDay.controller;

import TOTs.OOTDay.domain.ClothingRequest;
import TOTs.OOTDay.service.VertexAiStylingService;
import TOTs.OOTDay.util.stylingoption.domain.MoodDto;
import TOTs.OOTDay.util.stylingoption.domain.PlaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/styling")
public class StylingController {

    private final VertexAiStylingService service;

    @PostMapping
    public ResponseEntity<List<List<ClothingRequest>>> stylingCloth(@RequestBody List<ClothingRequest> imageList,
                                                                    @RequestBody List<MoodDto> mood,
                                                                    @RequestBody PlaceDto place) {
        try {
            List<List<ClothingRequest>> result = service.styleCloth(imageList, mood, place);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
