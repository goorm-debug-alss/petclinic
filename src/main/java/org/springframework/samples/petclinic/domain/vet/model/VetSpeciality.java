package org.springframework.samples.petclinic.domain.vet.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.samples.petclinic.model.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Entity
@Table(name = "vet_specialties")
public class VetSpeciality extends BaseEntity {
	@ManyToOne
	@JoinColumn(name = "vet_id")
	private Vet vet;

	@ManyToOne
	@JoinColumn(name = "specialty_id")
	private Specialty specialty;
}
