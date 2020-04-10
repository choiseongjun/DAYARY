package us.flower.dayary.repository.moim;

import org.springframework.data.jpa.repository.JpaRepository;

import us.flower.dayary.domain.Tag;

public interface HashTagRepository extends JpaRepository<Tag, Long>{

}
