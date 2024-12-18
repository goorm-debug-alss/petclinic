package org.springframework.samples.petclinic.domain.appointment.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "appointment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class Appointment extends BaseEntity {

	@Column(name = "appt_date", nullable = false)
	private LocalDate apptDate;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private ApptStatus status;

	@Column(name = "symptoms")
	private String symptoms;

	@ManyToOne
	@JoinColumn(name = "pet_id", nullable = false)
	private Pet petId;

	@ManyToOne
	@JoinColumn(name = "vet_id", nullable = false)
	private Vet vetId;

	public void updateAppointment(LocalDate apptDate, ApptStatus status, String symptoms, Pet petId, Vet vetId) {
		this.apptDate = apptDate;
		this.status = status;
		this.symptoms = symptoms;
		this.petId = petId;
		this.vetId = vetId;
	}
}

