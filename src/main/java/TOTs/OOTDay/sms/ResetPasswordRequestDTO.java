package TOTs.OOTDay.sms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {
    private String memberId;
    private String phoneNumber;
    private String newPassword;
    private String confirmPassword;
}
