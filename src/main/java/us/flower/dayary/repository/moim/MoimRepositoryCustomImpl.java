package us.flower.dayary.repository.moim;

import static us.flower.dayary.domain.QMoim.moim;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import us.flower.dayary.domain.Common;
import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.QMoim;

public class MoimRepositoryCustomImpl extends QuerydslRepositorySupport implements MoimRepositoryCustom{
	
	private final JPAQueryFactory jpaQueryFactory;
	
	public MoimRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
		super(Moim.class);
		this.jpaQueryFactory = jpaQueryFactory;
	}
 
	@Override
	public Page<Moim> findAllByCategoryQuery(Common common, Pageable pageable) {

		final QMoim moimlist = QMoim.moim;
		JPQLQuery query = from(moim)
						  .where(moim.category.eq(common));

		List<Moim> moimlists = getQuerydsl().applyPagination(pageable, query).fetch();
		long totalcount = query.fetchCount();
									  
		return new PageImpl<Moim>(moimlists,pageable,totalcount);
	}

}
//final JPQLQuery<Moim> query = from(moim)
//.select(Projections.constructor(Moim.class
//,moim.id
//,moim.category
//,moim.createdAt
//,moim.createDate
//,moim.imageExtension
//,moim.imageName
//,moim.imagePath
//,moim.intro
//,moim.peopleLimit
//,moim.moimpeople
//,moim.sidocode
//,moim.sidoname
//,moim.sigooncode
//,moim.sigoonname))
