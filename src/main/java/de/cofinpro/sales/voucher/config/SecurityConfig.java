package de.cofinpro.sales.voucher.config;

import de.cofinpro.sales.voucher.handler.VoucherServiceAccessDeniedHandler;
import de.cofinpro.sales.voucher.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final int ENCODER_STRENGTH = 13;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final VoucherServiceAccessDeniedHandler voucherServiceAccessDeniedHandler;

    @Autowired
    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                          VoucherServiceAccessDeniedHandler voucherServiceAccessDeniedHandler) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.voucherServiceAccessDeniedHandler = voucherServiceAccessDeniedHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/voucher-service/user/signup").permitAll()
                        //.requestMatchers(HttpMethod.GET, "/voucher-service/voucher").hasAnyRole()
                        .requestMatchers(HttpMethod.GET, "/voucher-service/user/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/voucher-service/user/role").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/voucher-service/user/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/actuator/shutdown").permitAll() // needs to run test
                        .anyRequest().permitAll()
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(voucherServiceAccessDeniedHandler).authenticationEntryPoint(restAuthenticationEntryPoint))
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) //to make accessible h2 console, it works as frame
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(ENCODER_STRENGTH);
    }
}
