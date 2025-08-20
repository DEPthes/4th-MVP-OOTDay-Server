package TOTs.OOTDay.sms.exception;

import TOTs.OOTDay.util.exception.BusinessBaseException;
import TOTs.OOTDay.util.exception.ErrorCode;

public class SmsException extends BusinessBaseException {
    public SmsException(ErrorCode errorCode) {
        super(errorCode);
    }
    public SmsException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
