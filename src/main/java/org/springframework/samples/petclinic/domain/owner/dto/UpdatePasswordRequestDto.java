package org.springframework.samples.petclinic.domain.owner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequestDto {
	private String currentPassword;
	private String newPassword;
}
