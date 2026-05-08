package sync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Configuration
public class JwtDecoderConfig {

    private static final String HMACSHA256 = "HmacSHA256";

    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.jwt.secret}")  String secret) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMACSHA256);
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(jwtValidator());
        return decoder;
    }

    private OAuth2TokenValidator<Jwt> jwtValidator() {
        return new DelegatingOAuth2TokenValidator<>(
                issuerValidator(),
                audienceValidator(),
                scopeValidator()
        );
    }

    private OAuth2TokenValidator<Jwt> issuerValidator() {
        return JwtValidators.createDefaultWithIssuer("financial-tracking-ws");
    }

    private OAuth2TokenValidator<Jwt> audienceValidator() {
        return jwt -> {
            if (jwt.getAudience().contains("financial-archive-service")) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Invalid audience", null)
            );
        };
    }

    private OAuth2TokenValidator<Jwt> scopeValidator() {
        return jwt -> {
            Collection<String> scopes = jwt.getClaimAsStringList("scope");

            if (scopes != null && scopes.contains("archive:write")) {
                return OAuth2TokenValidatorResult.success();
            }

            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_scope", "Missing scope", null)
            );
        };
    }

}
