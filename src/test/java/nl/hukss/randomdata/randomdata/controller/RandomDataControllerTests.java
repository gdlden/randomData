package nl.hukss.randomdata.randomdata.controller;

import jakarta.annotation.Resource;
import nl.hukss.randomdata.randomdata.vo.PersonalInfoVo;
import nl.hukss.randomdata.randomdata.vo.ResultData;
import nl.hukss.randomdata.randomdata.vo.ReturnCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class RandomDataControllerTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private RandomDataController randomDataController;

    @BeforeEach
    void setUpSchema() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS companies (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(255),
                    code VARCHAR(255),
                    registration_day VARCHAR(255),
                    character VARCHAR(255),
                    legal_representative VARCHAR(255),
                    capital VARCHAR(255),
                    business_scope TEXT,
                    province VARCHAR(255),
                    city VARCHAR(255),
                    address VARCHAR(255),
                    year VARCHAR(32),
                    personal_info_id BIGINT
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS personal_info (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(255),
                    sex VARCHAR(32),
                    mobile VARCHAR(64),
                    id_card VARCHAR(64),
                    credit_code VARCHAR(64),
                    company_name VARCHAR(255),
                    create_time VARCHAR(64),
                    bankcard VARCHAR(64),
                    user_id VARCHAR(255)
                )
                """);

        jdbcTemplate.execute("TRUNCATE TABLE personal_info, companies RESTART IDENTITY");
    }

    @Test
    void shouldGenerateAndPersistDataWhenGetOneCalled() {
        jdbcTemplate.update(
                "INSERT INTO companies (name, code, province, city, address, year, personal_info_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "Demo Co", "CODE-001", "Chongqing", "Chongqing", "Some Road", "2026", null
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteHost("host-a");

        ResultData<?> result = randomDataController.getOne("0", request);

        assertThat(result.getStatus()).isEqualTo(ReturnCode.RC100.getCode());
        PersonalInfoVo vo = (PersonalInfoVo) result.getData();
        assertThat(vo).isNotNull();
        assertThat(vo.getCreditCode()).isEqualTo("CODE-001");
        assertThat(vo.getProvince()).isEqualTo("Chongqing");
        assertThat(vo.getCity()).isEqualTo("Chongqing");
        assertThat(vo.getAddress()).isEqualTo("Some Road");

        Long savedCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM personal_info", Long.class);
        assertThat(savedCount).isEqualTo(1L);

        String persistedUserId = jdbcTemplate.queryForObject("SELECT user_id FROM personal_info LIMIT 1", String.class);
        assertThat(persistedUserId).isEqualTo("host-a");

        Long companyLinkedId = jdbcTemplate.queryForObject("SELECT personal_info_id FROM companies WHERE code = 'CODE-001'", Long.class);
        assertThat(companyLinkedId).isNotNull();
    }

    @Test
    void shouldUseProvidedUserIdWhenNotBlank() {
        jdbcTemplate.update(
                "INSERT INTO companies (name, code, province, city, address, year, personal_info_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "Demo Co", "CODE-002", "Chongqing", "Chongqing", "Addr", "2026", null
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteHost("host-b");

        randomDataController.getOne("explicit-user", request);

        String persistedUserId = jdbcTemplate.queryForObject("SELECT user_id FROM personal_info LIMIT 1", String.class);
        assertThat(persistedUserId).isEqualTo("explicit-user");
    }
}
