package nl.hukss.randomdata.randomdata;

import jakarta.annotation.Resource;
import nl.hukss.randomdata.randomdata.entity.Company;
import nl.hukss.randomdata.randomdata.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class RandomDataApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private CompanyService companyService;

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
        jdbcTemplate.execute("TRUNCATE TABLE companies RESTART IDENTITY");
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCompleteCrudOnRealPostgres() {
        Company company = new Company()
                .setName("TC Demo Co.")
                .setCode("TC-001")
                .setCity("Shanghai")
                .setProvince("Shanghai")
                .setYear("2026");

        assertThat(companyService.save(company)).isTrue();
        assertThat(company.getId()).isNotNull();

        Company created = companyService.getById(company.getId());
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("TC Demo Co.");
        assertThat(created.getCode()).isEqualTo("TC-001");

        created.setName("TC Demo Co. Updated");
        created.setCity("Hangzhou");
        assertThat(companyService.updateById(created)).isTrue();

        Company updated = companyService.getById(created.getId());
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("TC Demo Co. Updated");
        assertThat(updated.getCity()).isEqualTo("Hangzhou");

        assertThat(companyService.removeById(updated.getId())).isTrue();
        assertThat(companyService.getById(updated.getId())).isNull();
    }

    @Test
    void shouldQueryByConditionOnRealPostgres() {
        Company c1 = new Company().setName("Alpha").setCode("TC-Q-1").setCity("Shanghai").setYear("2026");
        Company c2 = new Company().setName("Beta").setCode("TC-Q-2").setCity("Beijing").setYear("2026");
        Company c3 = new Company().setName("Gamma").setCode("TC-Q-3").setCity("Shanghai").setYear("2025");

        assertThat(companyService.save(c1)).isTrue();
        assertThat(companyService.save(c2)).isTrue();
        assertThat(companyService.save(c3)).isTrue();

        Company shanghai2026 = companyService.lambdaQuery()
                .eq(Company::getCity, "Shanghai")
                .eq(Company::getYear, "2026")
                .one();
        assertThat(shanghai2026).isNotNull();
        assertThat(shanghai2026.getCode()).isEqualTo("TC-Q-1");

        long shanghaiCount = companyService.lambdaQuery()
                .eq(Company::getCity, "Shanghai")
                .count();
        assertThat(shanghaiCount).isEqualTo(2);
    }
}
