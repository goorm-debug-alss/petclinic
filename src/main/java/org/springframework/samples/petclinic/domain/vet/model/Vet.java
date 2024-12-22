package org.springframework.samples.petclinic.domain.vet.model;

import java.math.BigDecimal;

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
@Table(name = "vets")
public class Vet extends BaseEntity {
	@Column(length = 15, nullable = false)
	private String name;

	@Column(precision = 3, scale = 2)
	private BigDecimal averageRatings;

	private Integer reviewCount;

	public void updateRatings(BigDecimal newAverageRatings, Integer newReviewCount) {
		this.averageRatings = newAverageRatings;
		this.reviewCount = newReviewCount;
	}
}
