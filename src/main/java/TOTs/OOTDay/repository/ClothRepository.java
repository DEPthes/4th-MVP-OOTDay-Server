package TOTs.OOTDay.repository;

import TOTs.OOTDay.domain.Cloth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClothRepository extends JpaRepository<Cloth,Long> {
    void deleteByUuid(UUID uuid);

    Cloth findByUuid(UUID uuid);
}
