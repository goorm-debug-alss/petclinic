package org.springframework.samples.petclinic.domain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDto {
	private String accessToken;
	private LocalDateTime accessTokenExpiredAt;
	private String refreshToken;
	private LocalDateTime refreshTokenExpiredAt;
}
