package org.springframework.samples.petclinic.domain.owner.dto;

import lombok.*;
import org.springframework.samples.petclinic.domain.owner.model.Owner;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
	private String userId;
	private String password;
	private String name;
	private String address;
	private String city;
	private String telephone;

	public Owner toEntity() {
		return Owner.builder()
			.userId(userId)
			.password(password)
			.name(name)
			.address(address)
			.city(city)
			.telephone(telephone)
			.build();
	}
}
