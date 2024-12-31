package org.springframework.samples.petclinic.domain.appointment.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class Appointment extends BaseEntity {

	@Column(name = "appt_date", nullable = false)
	private LocalDateTime apptDateTime;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private ApptStatus status;

	@Column(name = "symptoms")
	private String symptoms;

	@ManyToOne
	@JoinColumn(name = "pet_id", nullable = false)
	private Pet pet;

	@ManyToOne
	@JoinColumn(name = "vet_id", nullable = false)
	private Vet vet;

	public void updateAppointment(LocalDateTime apptDateTime, ApptStatus status, String symptoms, Pet petId, Vet vetId) {
		this.apptDateTime = apptDateTime;
		this.status = status;
		this.symptoms = symptoms;
		this.pet = petId;
		this.vet = vetId;
	}
}

