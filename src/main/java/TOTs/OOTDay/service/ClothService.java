package TOTs.OOTDay.service;

import TOTs.OOTDay.domain.Cloth;
import TOTs.OOTDay.domain.GeminiClothingRequest;
import TOTs.OOTDay.repository.ClothRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClothService {

    private final ClothRepository clothRepository;

    @Transactional
    public Cloth saveCloth(GeminiClothingRequest request, MultipartFile file) {
        // s3 에 이미지 저장 코드...?

        Cloth cloth = Cloth.builder().name(request.getName()).category(request.getCategory())
                .mood(request.getMood()).description(request.getDescription()).build();

        return clothRepository.save(cloth);
    }

    public List<GeminiClothingRequest> findAll() {
        List<Cloth> list = clothRepository.findAll();
        List<GeminiClothingRequest> dtoList = new ArrayList<>();
        for (Cloth cloth : list) {
            GeminiClothingRequest dto = new GeminiClothingRequest(cloth.getUuid(), cloth.getCategory(),
                    cloth.getMood(), cloth.getDescription(),cloth.getName());

            dtoList.add(dto);
        }
        return dtoList;
    }

    @Transactional
    public void deleteCloth(UUID uuid) {
        clothRepository.deleteByUuid(uuid);
    }

}
