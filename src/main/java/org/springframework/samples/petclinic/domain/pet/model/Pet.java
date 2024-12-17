package org.springframework.samples.petclinic.domain.pet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "pets")
public class Pet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "birth_date")
	private String birthDate;

	@Column(name = "type_id", nullable = false)
	private Integer typeId;

	@Column(name = "owner_id", nullable = false)
	private Integer ownerId;
}
