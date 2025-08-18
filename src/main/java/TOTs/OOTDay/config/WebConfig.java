package TOTs.OOTDay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthInterceptor())
                .addPathPatterns("/**") // 모든 url에 적용
                .excludePathPatterns(
                        "/api/members/login", // 로그인
                        "/api/members/join", // 회원가입
                        "/api/members/find-id", // 아이디 찾기
                        "/api/members/reset-password/**", // 비밀번호 찾기
                        "/api/members/refresh", // Access Token 재발급
                        "/api/members/logout", // 로그아웃
                        "/sms/**", // sms 관련 모든 것
                        "/swagger-ui/**", // swagger
                        "/error"
                );
    }
}
