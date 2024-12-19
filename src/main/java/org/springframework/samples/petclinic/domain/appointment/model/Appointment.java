package org.springframework.samples.petclinic.domain.appointment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Entity
@Table(name = "appointment")
public class Appointment extends BaseEntity {
	private LocalDate apptDate;

	@Column(length = 50)
	@Enumerated(EnumType.STRING)
	private ApptStatus status;

	private String symptoms;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet petId;

	@ManyToOne
	@JoinColumn(name = "vet_id")
	private Vet vetId;
}
