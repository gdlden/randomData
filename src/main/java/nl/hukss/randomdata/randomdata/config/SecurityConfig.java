package nl.hukss.randomdata.randomdata.config;

import nl.hukss.randomdata.randomdata.filter.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 开启授权保护
                .authorizeHttpRequests(authorize -> authorize
                        // 不需要认证的地址有哪些
                        .requestMatchers(
                                "/blog/**",
                                "/public/**",
                                "/about",
                                "/user/login").permitAll()	// ** 通配符
                        // 对所有请求开启授权保护
                        .anyRequest()
                        // 已认证的请求会被自动授权
                        .authenticated()
                )
                .addFilterBefore(new JwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                // 使用默认的登陆登出页面进行授权登陆
                .formLogin(Customizer.withDefaults())
                // 启用“记住我”功能的。允许用户在关闭浏览器后，仍然保持登录状态，直到他们主动注销或超出设定的过期时间。
                .rememberMe(Customizer.withDefaults());
        // 关闭 csrf CSRF（跨站请求伪造）是一种网络攻击，攻击者通过欺骗已登录用户，诱使他们在不知情的情况下向受信任的网站发送请求。
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
