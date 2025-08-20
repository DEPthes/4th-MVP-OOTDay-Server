package TOTs.OOTDay.sms.exception;

import TOTs.OOTDay.util.exception.ErrorCode;

public class SmsCodeMismatch extends SmsException{
    public SmsCodeMismatch() {
        super(ErrorCode.INVALID_VERIFICATION_CODE);
    }
}
