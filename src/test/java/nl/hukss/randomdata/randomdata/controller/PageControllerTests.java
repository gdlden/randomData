package nl.hukss.randomdata.randomdata.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageControllerTests {
    @Test
    void shouldForwardLoginPageToFrontend() {
        assertThat(new PageController().login()).isEqualTo("forward:/index.html");
    }

    @Test
    void shouldForwardAppPageToFrontend() {
        assertThat(new PageController().app()).isEqualTo("forward:/index.html");
    }
}
