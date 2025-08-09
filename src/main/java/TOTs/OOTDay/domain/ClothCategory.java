 package TOTs.OOTDay.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClothCategory {
    TOP("상의"),
    BOTTOM("하의"),
    DRESS("드레스"),
    FASHION_ITEM("패션소품"),
    SHOES("신발"),
    ACCESSORY("악세사리");

    private final String label;

    ClothCategory(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}
