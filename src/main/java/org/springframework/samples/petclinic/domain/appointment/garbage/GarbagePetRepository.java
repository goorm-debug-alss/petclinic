package org.springframework.samples.petclinic.domain.appointment.garbage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.pet.model.Pet;
// 테스트용 입니다. PetRepository 구현되면 바로 삭제하겠습니다.
public interface GarbagePetRepository extends JpaRepository<Pet, Integer> {
}
