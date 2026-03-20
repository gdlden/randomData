package nl.hukss.randomdata.randomdata.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTests {

    @Test
    void shouldCreateAndParseJwtWithSubject() throws Exception {
        String subject = "12345";

        String token = JwtUtil.createJWT(subject);
        Claims claims = JwtUtil.parseJWT(token);

        assertThat(token).isNotBlank();
        assertThat(claims.getSubject()).isEqualTo(subject);
        assertThat(claims.getIssuer()).isEqualTo("sg");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void shouldGenerateUuidWithoutHyphen() {
        String uuid = JwtUtil.getUUID();

        assertThat(uuid).hasSize(32);
        assertThat(uuid).doesNotContain("-");
    }
}
