package us.flower.dayary.repository.moim;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import us.flower.dayary.domain.Common;
import us.flower.dayary.domain.Moim;

public interface MoimRepositoryCustom {

	Page<Moim> findAllByCategoryQuery(Common common, Pageable pageable);

}
