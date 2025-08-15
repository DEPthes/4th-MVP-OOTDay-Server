package TOTs.OOTDay.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static final String REFRESH_COOKIE = "refresh_token"; // 쿠키 이름

    public static void addRefreshTokenCookie(HttpServletResponse response, String token, int maxAgeSeconds) {
        Cookie cookie = new Cookie(REFRESH_COOKIE, token); // 쿠키 생성
        cookie.setHttpOnly(true); // 접근 X
        cookie.setSecure(true); // Https에서만 전송 권장
        cookie.setPath("/"); // 전체 경로
        cookie.setMaxAge(maxAgeSeconds); // 유효 기간
        response.addCookie(cookie); // 응답 쿠키 추가
    }

    public static void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_COOKIE, ""); // 빈값
        cookie.setHttpOnly(true); // 접근 X
        cookie.setSecure(true); // Https에서만 전송 권장
        cookie.setPath("/"); // 전체 경로
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie); // 응답 쿠키 추가
    }
}
