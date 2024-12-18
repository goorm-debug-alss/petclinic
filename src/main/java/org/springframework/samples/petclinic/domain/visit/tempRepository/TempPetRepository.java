package org.springframework.samples.petclinic.domain.visit.tempRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;

//테스트용 임시저장소
public interface TempPetRepository extends JpaRepository<Pet,Integer> {
}
