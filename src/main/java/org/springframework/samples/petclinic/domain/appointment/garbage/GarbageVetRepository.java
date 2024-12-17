package org.springframework.samples.petclinic.domain.appointment.garbage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.domain.vet.model.Vet;
// 테스트용 입니다. VetRepository 구현되면 바로 삭제하겠습니다.
public interface GarbageVetRepository extends JpaRepository<Vet, Integer> {
}
