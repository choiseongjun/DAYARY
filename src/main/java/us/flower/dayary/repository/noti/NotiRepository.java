package us.flower.dayary.repository.noti;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import us.flower.dayary.domain.Noti;

public interface NotiRepository extends JpaRepository<Noti, Long>{
	List<Noti> findByMoim_idAndGubunCd(long no,char cd);
	List<Noti> findByPeople_idAndGubunCd(Pageable pageable,long id,char cd);
	int countByPeople_idAndGubunCdAndReadYn(long id,char cd,char yn);
}
