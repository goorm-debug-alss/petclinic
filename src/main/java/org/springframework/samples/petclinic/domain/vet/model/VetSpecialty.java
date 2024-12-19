
package org.springframework.samples.petclinic.domain.vet.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.domain.speciality.model.Specialty;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Entity
@Table(name = "vet_specialties")
public class VetSpecialty extends BaseEntity {
	@ManyToOne
	@JoinColumn(name = "vet_id")
	private Vet vetId;

	@ManyToOne
	@JoinColumn(name = "specialty_id")
	private Specialty specialtyId;
}
