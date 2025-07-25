package TOTs.OOTDay.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

// JWT
public class JwtUtil {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);;  //JWT 비밀키

    //JWT 만료 시간 --> 1시간
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    // memberId를 기반으로 JWT 토큰 생성
    public static String generateToken(String memberId) {
        return Jwts.builder()
                .setSubject(memberId)                           // 토큰 --> 사용자 아이디
                .setIssuedAt(new Date())                        // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
                .signWith(SECRET_KEY) // 서명 알고리즘과 키
                .compact();                                     // 토큰 문자열로 반환
    }
}
