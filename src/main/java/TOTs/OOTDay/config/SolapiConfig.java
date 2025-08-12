package TOTs.OOTDay.config;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolapiConfig {

    @Value("${solapi.apiKey}")
    private String rawApiKey;

    @Value("${solapi.apiSecret}")
    private String rawApiSecret;

    private static String sanitize(String s) {
        if (s == null) return null;
        // 앞뒤 공백 제거 + Zero-Width, BOM 제거
        return s.trim().replaceAll("[\\u200B-\\u200D\\uFEFF]", "");
    }

    private static String mask(String s) {
        if (s == null || s.length() < 4) return "****";
        return "****" + s.substring(s.length() - 4);
    }

    @Bean
    public DefaultMessageService defaultMessageService() {
        String apiKey   = sanitize(rawApiKey);
        String apiSecret= sanitize(rawApiSecret);

        // 잘되는지만 확인
        System.out.println("[Solapi] apiKey=" + mask(apiKey) + ", apiSecret=" + mask(apiSecret));
        System.out.println("[Solapi] baseUrl=https://api.solapi.com");
        System.out.println("[Solapi] jvmTime=" + java.time.ZonedDateTime.now());

        // 값이 없을 경우 확인하기
        if (apiKey == null || apiKey.isEmpty())   throw new IllegalStateException("solapi.apiKey is empty");
        if (apiSecret == null || apiSecret.isEmpty()) throw new IllegalStateException("solapi.apiSecret is empty");

        return NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.solapi.com");
    }
}
