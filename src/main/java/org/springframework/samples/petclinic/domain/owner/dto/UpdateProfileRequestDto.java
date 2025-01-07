package org.springframework.samples.petclinic.domain.owner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDto {
	private String name;
	private String address;
	private String city;
	private String telephone;
}
