package org.springframework.samples.petclinic.domain.pet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.pet.enums.PetStatus;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Entity
@Table(name = "pets")
public class Pet extends BaseEntity {


	@Column(length = 15, nullable = false)
	private String name;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

	@ManyToOne
	@JoinColumn(name = "type_id")
	private PetType type;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Owner owner;

	@Enumerated(EnumType.STRING)
	@Column(length = 10, nullable = false)
	private PetStatus status;

	@PrePersist
	public void prePersist() {
		if (this.status == null) {
			this.status = PetStatus.REGISTERED; // 명시적으로 기본값 적용
		}
	}

	public boolean isRegistered() {
		return Objects.equals(this.status, PetStatus.REGISTERED);
	}
}
