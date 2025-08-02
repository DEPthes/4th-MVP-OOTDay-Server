package TOTs.OOTDay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FIndAccountRequestDTO {
    private String phoneNumber;
    private String type; // id인지 password인지
}
