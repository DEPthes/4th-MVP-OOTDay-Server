package TOTs.OOTDay.service;

import TOTs.OOTDay.domain.Cloth;
import TOTs.OOTDay.domain.ClothingRequest;
import TOTs.OOTDay.repository.ClothRepository;
import TOTs.OOTDay.util.s3.S3DomainType;
import TOTs.OOTDay.util.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ClothService {

    private final ClothRepository clothRepository;
    private final S3Service s3Service;

    @Transactional
    public Cloth saveCloth(ClothingRequest request, MultipartFile file) throws IOException {

        Cloth temp = Cloth.builder()
                .name(request.getName())
                .category(request.getCategory())
                .mood(request.getMood())
                .description(request.getDescription())
                .build();
        temp.generateUuid();

        // s3 에 이미지 저장 코드
        String imageUrl = s3Service.uploadFile(file, S3DomainType.CLOTHES, temp.getUuid());

        Cloth cloth = Cloth.builder()
                .imageUrl(imageUrl)
                .name(temp.getName())
                .category(temp.getCategory())
                .mood(temp.getMood())
                .description(temp.getDescription())
                .build();

        return clothRepository.save(cloth);
    }

    public List<ClothingRequest> findAll() {
        List<Cloth> list = clothRepository.findAll();
        List<ClothingRequest> dtoList = new ArrayList<>();
        for (Cloth cloth : list) {
            ClothingRequest dto = new ClothingRequest(cloth.getUuid(), cloth.getName(),
                    cloth.getCategory(), cloth.getMood(),cloth.getDescription(), cloth.getImageUrl());

            dtoList.add(dto);
        }
        return dtoList;
    }

    @Transactional
    public void deleteCloth(UUID uuid) {

        //s3 이미지 파일 삭제
        Cloth cloth = clothRepository.findByUuid(uuid);
        String imageUrl = cloth.getImageUrl();
        String[] split = imageUrl.split("/");
        String prefix = split[split.length - 4] + "/" + split[split.length - 3] + "/" + split[split.length - 2];
        s3Service.deleteFolder(prefix);

        clothRepository.deleteByUuid(uuid);
    }

}
