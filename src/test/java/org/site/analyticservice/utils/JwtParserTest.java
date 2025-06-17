package org.site.analyticservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtParserTest {

    @InjectMocks
    private JwtParser jwtParser;

    private final String secretKey = "u8myvBQtu0SlFpN3kB+wEg3qYN5zp1sO/e4TrRkK3J8=";


    @BeforeEach
    void setUp() throws Exception {
        var field = JwtParser.class.getDeclaredField("secretKey");
        field.setAccessible(true);
        field.set(jwtParser, secretKey);
    }

    @Test
    void extractUserId_shouldReturnUserIdFromToken() {
        String userId = "12345";

        String token = createTestToken(Map.of("userId", userId));

        String extractedUserId = jwtParser.extractUserId(token);

        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    void extractAuthority_shouldReturnAuthoritiesFromToken() {
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

        String token = createTestToken(Map.of("authorities", roles));

        Collection<? extends GrantedAuthority> authorities = jwtParser.extractAuthority(token);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrderElementsOf(roles);
    }

    @Test
    void extractAuthority_shouldReturnEmptyListIfNoAuthorities() {
        String token = createTestToken(Map.of());

        Collection<? extends GrantedAuthority> authorities = jwtParser.extractAuthority(token);

        assertThat(authorities).isEmpty();
    }

    @Test
    void extractUserName_shouldReturnSubjectFromToken() {
        String subject = "testUser";

        String token = createTestToken(Map.of(), subject);

        String extractedSubject = jwtParser.extractUserName(token);

        assertThat(extractedSubject).isEqualTo(subject);
    }

    @Test
    void accessTokenExtractor_shouldExtractAccessTokenFromCookieHeader() {
        Map<String, String> headers = Map.of(
                "cookie", "access_token=token123; other_cookie=val"
        );

        String extracted = jwtParser.accessTokenExtractor(headers);

        assertThat(extracted).isEqualTo("token123");
    }

    @Test
    void accessTokenExtractor_shouldReturnNullIfNoAccessTokenCookie() {
        Map<String, String> headers = Map.of(
                "cookie", "some_cookie=val; another=val2"
        );

        String extracted = jwtParser.accessTokenExtractor(headers);

        assertThat(extracted).isNull();
    }

    @Test
    void accessTokenExtractor_shouldReturnNullIfNoCookieHeader() {
        Map<String, String> headers = Map.of();

        String extracted = jwtParser.accessTokenExtractor(headers);

        assertThat(extracted).isNull();
    }

    @Test
    void extractAllClaims_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.string";

        assertThatThrownBy(() -> jwtParser.extractUserId(invalidToken))
                .isInstanceOf(RuntimeException.class);
    }

    private String createTestToken(Map<String, Object> claims) {
        return createTestToken(claims, "subject");
    }

    private String createTestToken(Map<String, Object> claims, String subject) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .signWith(key)
                .compact();
    }
}
