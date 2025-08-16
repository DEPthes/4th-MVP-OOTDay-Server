package TOTs.OOTDay.styling.stylingoption.service;

import TOTs.OOTDay.styling.stylingoption.domain.Place;
import TOTs.OOTDay.styling.stylingoption.domain.PlaceDto;
import TOTs.OOTDay.styling.stylingoption.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository repository;

    @Transactional
    public void save(String input) {
        repository.save(new Place(input));
    }

    public List<PlaceDto> findAll() {
        List<PlaceDto> list = new ArrayList<>();
        List<Place> all = repository.findAll();
        for (Place place : all) {
            list.add(new PlaceDto(place.getPlaceName()));
        }

        return list;
    }
}
