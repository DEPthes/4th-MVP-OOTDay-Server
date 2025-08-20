package TOTs.OOTDay.sms.exception;

import TOTs.OOTDay.util.exception.ErrorCode;

public class SmsCodeNotSent extends SmsException {
    public SmsCodeNotSent() {
        super(ErrorCode.CODE_NOT_SENT);
    }
}
