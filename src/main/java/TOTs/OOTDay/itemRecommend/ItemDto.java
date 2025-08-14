package TOTs.OOTDay.itemRecommend;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ItemDto {
    private String name;

    private String imageUrl;

    private String purchaseUrl;
}
