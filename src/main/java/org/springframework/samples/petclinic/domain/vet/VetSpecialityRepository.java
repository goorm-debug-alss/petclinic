package org.springframework.samples.petclinic.domain.vet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
import org.springframework.samples.petclinic.domain.vet.model.VetSpecialty;

import java.util.List;

public interface VetSpecialityRepository extends JpaRepository<VetSpecialty, Integer> {
	List<VetSpecialty> findVetSpecialtiesByVetId_Id(int vetId);

	void deleteAllByVetId_Id(int vetId);

	// 전문분야별 수의사 찾기
	List<VetSpecialty> findVetIdsBySpecialtyId_Id(int specialtyId);
}
