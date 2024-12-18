package org.springframework.samples.petclinic.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.samples.petclinic.domain.token.service.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Authorization Interceptor url : {}", request.getRequestURL());

        // WEB, chrome의 경우 GET, POST OPTIONS = pass
        if(HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // js, html, png resource를 요청하는 경우 = pass
        if(handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        // header 검증
        var token = request.getHeader("authorization-token");
        if(token == null) {
			throw new IllegalAccessException("Token is Null");
        }

        var ownerId = tokenService.validationToken(token);

        if(ownerId != null) {
            // context에 저장
            var requestContext = Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
            requestContext.setAttribute("ownerId", ownerId, RequestAttributes.SCOPE_REQUEST);

            return true; // 유효한 경우 통과 처리
        }

		throw new IllegalAccessException("Validation Failed");
    }
}
