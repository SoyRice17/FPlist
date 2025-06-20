package inhatc.fplist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/admin/**").hasRole("ADMIN")  // 관리자 페이지는 ADMIN 권한 필요
                .anyRequest().permitAll()  // 나머지는 모두 허용
            )
            .formLogin(form -> form
                .loginPage("/admin/login")  // 커스텀 로그인 페이지
                .loginProcessingUrl("/admin/perform_login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/admin/api/**")  // API 요청은 CSRF 제외
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 관리자 계정 하나만 생성 (환경변수나 properties에서 읽어오는 것을 권장)
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("fplist2024!"))  // 강력한 비밀번호 사용
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 