package org.springframework.samples.petclinic.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security의 설정을 정의
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * Spring Security의 필터 체인을 정의
	 *  <p>
	 * - CSRF를 비활성.<br>
	 * - 특정 경로(회원가입, 로그인, Swagger 경로)는 인증 없이 접근 가능하도록 설정<br>
	 * - 나머지 요청은 인증이 필요<br>
	 * </p>
	 *
	 * @param http HttpSecurity 객체를 통해 보안 설정을 정의
	 * @return SecurityFilterChain Spring Security 필터 체인을 반환
	 * @throws Exception 보안 설정 중 발생할 수 있는 예외
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/swagger-ui.html",
					"/owner/register",
					"/owner/login"
				).permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}


	/**
	 * 비밀번호 암호화를 위한 PasswordEncoder Bean
	 *
	 * @return PasswordEncoder BCryptPasswordEncoder 인스턴스를 반환
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
