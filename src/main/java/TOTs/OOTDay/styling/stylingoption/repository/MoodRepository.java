package TOTs.OOTDay.styling.stylingoption.repository;

import TOTs.OOTDay.styling.stylingoption.domain.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodRepository extends JpaRepository<Mood, Long> {
}
