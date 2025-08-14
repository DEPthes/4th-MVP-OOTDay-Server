package TOTs.OOTDay.itemRecommend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingRequest {

    private String query = "";
    private Integer display = 1;

    public MultiValueMap map() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("query", query);
        map.add("display", String.valueOf(display));

        return map;
    }
}
