package TOTs.OOTDay.util.stylingoption.service;

import TOTs.OOTDay.util.stylingoption.domain.Mood;
import TOTs.OOTDay.util.stylingoption.domain.MoodDto;
import TOTs.OOTDay.util.stylingoption.domain.Place;
import TOTs.OOTDay.util.stylingoption.domain.PlaceDto;
import TOTs.OOTDay.util.stylingoption.repository.MoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MoodService {

    private final MoodRepository repository;

    @Transactional
    public void save(String input) {
        repository.save(new Mood(input));
    }

    public List<MoodDto> findAll() {
        List<MoodDto> list = new ArrayList<>();
        List<Mood> all = repository.findAll();
        for (Mood mood : all) {
            list.add(new MoodDto(mood.getMoodName()));
        }

        return list;
    }
}
