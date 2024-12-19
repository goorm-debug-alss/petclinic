package org.springframework.samples.petclinic.config.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.interceptor.AuthorizationInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
	private final AuthorizationInterceptor authorizationInterceptor;

	private List<String> OPEN_API = List.of(
		"/owner/register",
		"/owner/login"
	);

	private List<String> DEFAULT_EXCLUDE = List.of(
		"/",
		"favicon.ico",
		"/error"
	);

	private List<String> SWAGGER = List.of(
		"/swagger-ui.html",
		"/swagger-ui/**",
		"/v3/api-docs/**"
	);


	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(authorizationInterceptor)
			.excludePathPatterns(OPEN_API)
			.excludePathPatterns(DEFAULT_EXCLUDE)
			.excludePathPatterns(SWAGGER);
	}

	// CORS 설정 추가
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 엔드포인트 허용
			.allowedOriginPatterns("*") // 모든 오리진 허용
			.allowedMethods("GET", "POST", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
			.allowedHeaders("*") // 모든 헤더 허용
			.allowCredentials(true); // 쿠키 허용
	}
}
