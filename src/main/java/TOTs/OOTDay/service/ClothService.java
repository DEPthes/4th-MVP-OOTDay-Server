package TOTs.OOTDay.service;

import TOTs.OOTDay.domain.Cloth;
import TOTs.OOTDay.repository.ClothRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClothService {

    private final ClothRepository clothRepository;

    @Transactional
    public Cloth saveCloth(Cloth cloth, MultipartFile file) {
        // s3 에 이미지 저장 코드...?

        return clothRepository.save(cloth);
    }

    public List<Cloth> findAll() {
        return clothRepository.findAll();
    }

    @Transactional
    public void deleteCloth(Long id) {
        clothRepository.deleteById(id);
    }

}
