package TOTs.OOTDay.sms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordSendCodeDTO {
    private String memberId;
    private String phoneNumber;
}
