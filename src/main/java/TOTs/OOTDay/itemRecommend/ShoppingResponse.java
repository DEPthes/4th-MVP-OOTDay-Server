package TOTs.OOTDay.itemRecommend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingResponse {

    private Date lastBuildDate;
    private Integer total;
    private Integer start;
    private Integer display;
    private List<ShoppingItem> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShoppingItem {
        private String title;
        private String link;
        private String image;
        private Integer lprice;
        private Integer hprice;
        private String mallName;
        private String maker;
        private String brand;
    }
}