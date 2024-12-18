package org.springframework.samples.petclinic.domain.owner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.samples.petclinic.domain.owner.model.Owner;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
	private String userId;
	private String password;
}
