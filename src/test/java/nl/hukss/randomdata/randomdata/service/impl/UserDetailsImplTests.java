package nl.hukss.randomdata.randomdata.service.impl;

import nl.hukss.randomdata.randomdata.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTests {

    @Test
    void shouldExposeUserFieldsAndDefaultFlags() {
        User user = new User()
                .setId(1L)
                .setUsername("alice")
                .setPassword("secret");

        UserDetailsImpl details = new UserDetailsImpl(user);

        assertThat(details.getUsername()).isEqualTo("alice");
        assertThat(details.getPassword()).isEqualTo("secret");
        assertThat(details.getAuthorities()).isEmpty();
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getUser()).isEqualTo(user);
    }
}
