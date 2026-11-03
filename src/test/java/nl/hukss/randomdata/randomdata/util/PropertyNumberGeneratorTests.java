package nl.hukss.randomdata.randomdata.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyNumberGeneratorTests {

    @Test
    void shouldGenerateFactoryBuildingNumberInCommonIndustrialParkFormat() {
        String number = PropertyNumberGenerator.randomFactoryBuildingNo();

        assertThat(number).matches("[A-H]区\\d{2}栋-\\d{2}号厂房");
    }

    @Test
    void shouldGeneratePropertyCertificateNumberForChongqing() {
        String number = PropertyNumberGenerator.randomPropertyCertificateNo("重庆市", "重庆");

        assertThat(number).matches("渝（20\\d{2}）重庆市不动产权第\\d{8}号");
    }

    @Test
    void shouldSupportEnglishChongqingLocationFromImportedCompanyData() {
        String number = PropertyNumberGenerator.randomPropertyCertificateNo("Chongqing", "Chongqing");

        assertThat(number).matches("渝（20\\d{2}）重庆市不动产权第\\d{8}号");
    }
}
