package org.springframework.samples.petclinic.domain.review.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.samples.petclinic.domain.owner.model.Owner;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Review extends BaseEntity {

	@Column(name = "score", nullable = false)
	private Integer score;

	@Column(name = "content", length = 200, nullable = false)
	private String content;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	@ManyToOne
	@JoinColumn(name = "vet_id", nullable = false)
	private Vet vetId;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private Owner ownerId;

	public void updateReview(Integer score, String content, Owner ownerId, Vet vetId) {
		this.score = score;
		this.content = content;
		this.vetId = vetId;
		this.ownerId = ownerId;
	}
}
