package TOTs.OOTDay.itemRecommend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemController {

    private final VertexAiItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> uploadCloth(@RequestPart("image") MultipartFile image) throws IOException {
        try {
            ItemDto itemDto = service.itemRecommend(image);
            return ResponseEntity.ok(itemDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
