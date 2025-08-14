package TOTs.OOTDay.util.stylingoption.repository;

import TOTs.OOTDay.util.stylingoption.domain.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodRepository extends JpaRepository<Mood, Long> {
}
