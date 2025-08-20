package TOTs.OOTDay.util.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 서버 에러 (S)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "[OOTDay] 내부 서버 에러가 발생했습니다."),
    API_UNKNOWN_FINISH_REASON(HttpStatus.INTERNAL_SERVER_ERROR, "API_UNKNOWN_FINISH_REASON", "[OOTDay] 알 수 없는 이유로 응답을 불러올 수 없습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "[OOTDay] 데이터베이스 오류가 발생했습니다."),

    // JWT 관련 에러
    INVALID_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN_FORMAT", "[OOTDay] JWT 형식이 올바르지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "[OOTDay] JWT 토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "[OOTDay] 유효하지 않거나 일반적이지 않은 JWT 토큰입니다."),
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "TOKEN_MALFORMED", "[OOTDay] JWT 토큰이 변조되었거나 형식이 올바르지 않습니다."),
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_SIGNATURE_INVALID", "[OOTDay] JWT 토큰의 서명이 유효하지 않습니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "TOKEN_UNSUPPORTED", "[OOTDay] 지원되지 않는 JWT 토큰입니다."),
    TOKEN_NOT_PROVIDED(HttpStatus.UNAUTHORIZED, "TOKEN_NOT_PROVIDED", "[OOTDay] JWT 토큰이 제공되지 않았습니다."),
    NOT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "NOT_REFRESH_TOKEN", "[OOTDay] 제공된 토큰은 Refresh Token이 아닙니다."),
    REFRESH_TOKEN_PARSE_FAILED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_PARSE_FAILED", "[OOTDay] Refresh Token에서 사용자 정보를 추출할 수 없습니다."),

    // 마이페이지 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "[OOTDay] 해당 사용자를 찾을 수 없습니다."),

    // 요청 에러 (R)
    INVALID_PARAM_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_PARAM_REQUEST", "[OOTDay] 요청된 파람 값이 잘못되었습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "[OOTDay] 요청된 값이 잘못되었습니다."),
    OFFSET_IS_LESS_THEN_ONE(HttpStatus.BAD_REQUEST, "OFFSET_IS_LESS_THEN_ONE", "[OOTDay] offset은 1부터 시작합니다."),
    LIMIT_IS_LESS_THEN_ONE(HttpStatus.BAD_REQUEST, "LIMIT_IS_LESS_THEN_ONE", "[OOTDay] limit은 1부터 시작합니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "[OOTDay] 잘못된 입력입니다."),
    EMAIL_IS_INVALID(HttpStatus.BAD_REQUEST, "EMAIL_IS_INVALID", "[OOTDay] 잘못된 이메일 형식입니다."),
    NICKNAME_IS_INVALID(HttpStatus.BAD_REQUEST, "EMAIL_IS_INVALID", "[OOTDay] 회원 닉네임이 잘못된 형식입니다."),
    INVALID_S3_URL(HttpStatus.BAD_REQUEST, "INVALID_S3_URL", "[OOTDay] 유효하지 않은 S3 Url입니다."),

    // S3 이미지 증복 에러
    S3_IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_IMAGE_UPLOAD_FAILED", "[OOTDay] S3 이미지 업로드에서 에러가 발생하였습니다."),
    
    
    // 회원 에러(M)
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"MEMBER_NOT_FOUND" ,"[OOTDay] 회원 정보를 찾을 수 없습니다." ),
    PASSWORD_IS_INVALID(HttpStatus.BAD_REQUEST, "PASSWORD_IS_INVALID", "[OOTDay] 비밀번호가 잘못되었습니다" ),
    SAME_PASSWORD_NOT_ALLOWED(HttpStatus.BAD_REQUEST,"SAME_PASSWORD_NOT_ALLOWED" , "[OOTDay] 이전과 동일한 비밀번호로는 변경할 수 없습니다."),
    DUPLICATE_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST,"DUPLICATE_EMAIL_EXCEPTION", "[OOTDay] 이미 존재하는 이메일입니다." );





    private final HttpStatus status;
    private final String error;
    private final String message;

    ErrorCode(final HttpStatus status, final String error, final String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
