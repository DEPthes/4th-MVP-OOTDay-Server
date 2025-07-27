package TOTs.OOTDay.controller;

import TOTs.OOTDay.domain.Cloth;
import TOTs.OOTDay.domain.ClothingRequest;
import TOTs.OOTDay.service.ClothService;
import TOTs.OOTDay.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cloth")
public class ClothController {

    private final ClothService clothService;
    private final OpenAiService aiService;

    @PostMapping
    // 사진 받아서 gemini 한테 보낸 후 받은 정보들을 db에 저장(사진도 일단 clothService 서비스에 같이 보냄)
    public ResponseEntity<Cloth> uploadCloth(@RequestPart("image") MultipartFile image) {
        ClothingRequest geminiInfo = aiService.analyzeCloth(image);

        Cloth saved = clothService.saveCloth(geminiInfo, image);

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<ClothingRequest>> getAllCloth() {
        return ResponseEntity.ok(clothService.findAll());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Cloth> deleteCloth(@PathVariable UUID uuid) {
        clothService.deleteCloth(uuid);
        return ResponseEntity.ok().build();
    }
}
