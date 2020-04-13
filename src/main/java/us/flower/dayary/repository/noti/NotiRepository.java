package us.flower.dayary.repository.noti;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import us.flower.dayary.domain.Noti;

public interface NotiRepository extends JpaRepository<Noti, Long>{
	List<Noti> findByMoim_idAndGubunCd(long no,char cd);
	List<Noti> findByPeople_id(long id);
	int countByPeople_id(long id);
}
