package org.springframework.samples.petclinic.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.domain.token.service.TokenService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증을 처리하는 커스텀 필터
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenService tokenService;


	/**
	 * HTTP 요청에서 JWT 토큰을 처리하고 인증을 수행
	 *
	 * @param request		HTTP 요청 객체
	 * @param response		HTTP 응답 객체
	 * @param filterChain	필터 체인 객체
	 * @throws ServletException	필터 처리 중 발생한 서블릿 예외
	 * @throws IOException		필터 처리 중 발생한 I/O 예외
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		String token = request.getHeader("Authorization");

		if (token != null && !token.isEmpty()) {
			try {
				Integer ownerId = tokenService.validationToken(token);
				UserDetails userDetails = User.withUsername(ownerId.toString()).password("").roles("USER").build();
				SecurityContextHolder.getContext().setAuthentication(new TokenBasedAuthentication(userDetails));
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}
}
