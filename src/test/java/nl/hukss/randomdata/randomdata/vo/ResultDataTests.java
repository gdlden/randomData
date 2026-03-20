package nl.hukss.randomdata.randomdata.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultDataTests {

    @Test
    void shouldReturnSuccessPayloadAndStatus() {
        ResultData<String> result = ResultData.success("ok");

        assertThat(result.getStatus()).isEqualTo(ReturnCode.RC100.getCode());
        assertThat(result.getMessage()).isEqualTo(ReturnCode.RC100.getMessage());
        assertThat(result.getData()).isEqualTo("ok");
        assertThat(result.getTimestamp()).isPositive();
    }

    @Test
    void shouldReturnFailCodeAndMessage() {
        ResultData<Void> result = ResultData.fail(500, "boom");

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("boom");
        assertThat(result.getData()).isNull();
        assertThat(result.getTimestamp()).isPositive();
    }
}
