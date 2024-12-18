package org.springframework.samples.petclinic.domain.appointment.model;

import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
=======
import lombok.*;
>>>>>>> 08c7306 (Initial commit)
import lombok.experimental.SuperBuilder;
import org.springframework.samples.petclinic.domain.appointment.model.enums.ApptStatus;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.time.LocalDate;

<<<<<<< HEAD
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
=======
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

	public Appointment(LocalDate apptDate, ApptStatus status, String symptoms, Pet petId, Vet vetId) {
		this.apptDate = apptDate;
		this.status = status;
		this.symptoms = symptoms;
		this.petId = petId;
		this.vetId = vetId;
	}
}

>>>>>>> 08c7306 (Initial commit)
