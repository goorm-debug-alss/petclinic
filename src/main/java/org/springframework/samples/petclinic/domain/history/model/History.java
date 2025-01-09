package org.springframework.samples.petclinic.domain.history.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.visit.model.Visit;
import org.springframework.samples.petclinic.model.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Entity
@Table(name = "history")
public class History extends BaseEntity {
	private String symptoms;

	private String content;

	@ManyToOne
	@JoinColumn(name = "vet_id")
	private Vet vet;

	@OneToOne
	@JoinColumn(name = "visit_id")
	private Visit visit;
}
