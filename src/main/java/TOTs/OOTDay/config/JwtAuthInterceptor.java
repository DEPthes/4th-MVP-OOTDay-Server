package TOTs.OOTDay.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtAuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        System.out.println("요청 URI: " + uri);

        // 인증 X 허용(로그인, 회원가입, 아이디/비밀번호 찾기, sms 관련 모든 것)
        if (uri.startsWith("/api/members/login") ||
                uri.startsWith("/api/members/join") ||
                uri.startsWith("/api/members/find-id") ||
                uri.startsWith("/api/members/reset-password") ||
                uri.startsWith("/sms/")) {
            return true;
        }

        // Authorization 헤더 확인
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"message\": \"로그인 후에 사용하실 수 있습니다.\"}");
            return false;
        }
        String token = authHeader.substring(7);

        // JWT 유효성 검사
        try {
            JwtUtil.getMemberIdFromToken(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"message\": \"로그인 후에 사용하실 수 있습니다.\"}");
            return false;
        }
        return true;
    }
}
