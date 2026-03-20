package nl.hukss.randomdata.randomdata.service.impl;

import jakarta.annotation.Resource;
import nl.hukss.randomdata.randomdata.entity.User;
import nl.hukss.randomdata.randomdata.filter.JwtAuthenticationTokenFilter;
import nl.hukss.randomdata.randomdata.service.UserService;
import nl.hukss.randomdata.randomdata.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
class UserDetailsServiceImplTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Resource
    private UserService userService;

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @BeforeEach
    void setUpSchema() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    nick_name VARCHAR(255),
                    avatar VARCHAR(255)
                )
                """);
        jdbcTemplate.execute("TRUNCATE TABLE users");
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldLoadUserByUsernameFromRealPostgres() {
        jdbcTemplate.update(
                "INSERT INTO users (id, username, password, nick_name, avatar) VALUES (?, ?, ?, ?, ?)",
                10L, "tom", "{noop}pwd", "Tom", ""
        );

        UserDetailsImpl details = (UserDetailsImpl) userDetailsService.loadUserByUsername("tom");

        assertThat(details.getUsername()).isEqualTo("tom");
        assertThat(details.getPassword()).isEqualTo("{noop}pwd");
        assertThat(details.getUser().getId()).isEqualTo(10L);
    }

    @Test
    void shouldThrowWhenUserNotFoundInRealPostgres() {
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("missing");
    }

    @Test
    void shouldLoginAndReturnJwtWithUserId() throws Exception {
        jdbcTemplate.update(
                "INSERT INTO users (id, username, password, nick_name, avatar) VALUES (?, ?, ?, ?, ?)",
                99L, "alice", "{noop}pwd", "Alice", ""
        );

        String jwt = userService.login(new User().setUsername("alice").setPassword("pwd"));

        assertThat(jwt).isNotBlank();
        assertThat(JwtUtil.parseJWT(jwt).getSubject()).isEqualTo("99");
    }

    @Test
    void shouldSetSecurityContextWhenFilterReceivesValidToken() throws Exception {
        jdbcTemplate.update(
                "INSERT INTO users (id, username, password, nick_name, avatar) VALUES (?, ?, ?, ?, ?)",
                7L, "bob", "{noop}p", "Bob", ""
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + JwtUtil.createJWT("7"));

        jwtAuthenticationTokenFilter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .isEqualTo("bob");
    }
}
