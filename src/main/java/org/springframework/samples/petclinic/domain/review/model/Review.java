package org.springframework.samples.petclinic.domain.review.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Entity
@Table(name = "review")
public class Review extends BaseEntity {
	private Integer score;

	private String content;

	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "vet_id")
	private Vet vetId;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Owner ownerId;
}
