package us.flower.dayary.repository.moim;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimTag;

public interface MoimTagRepository extends JpaRepository<MoimTag,Long>{
	void deleteByMoim(Moim moim);
	List<MoimTag> findByMoim_id(long id);
}
