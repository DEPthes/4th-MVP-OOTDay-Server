package TOTs.OOTDay.styling;

import TOTs.OOTDay.wardrobe.ClothingRequest;
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
    public ResponseEntity<List<List<ClothingRequest>>> stylingCloth(@RequestBody StylingRequestDto request) {
        try {
            List<List<ClothingRequest>> result = service.styleCloth(
                    request.getImageList(),
                    request.getMood(),
                    request.getPlace()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
