package us.flower.dayary.repository.community;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import us.flower.dayary.domain.BoardGroup;
import us.flower.dayary.domain.CommunityBoard;
import us.flower.dayary.domain.DTO.BoardListDTO;

import static us.flower.dayary.domain.QCommunityBoard.communityBoard;
import static us.flower.dayary.domain.QCommunityBoardReply.communityBoardReply;
public class CommunityBoardRepositoryCustomImpl extends QuerydslRepositorySupport implements  CommunityBoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public CommunityBoardRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        super(CommunityBoard.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }



    @Override
    public Page<BoardListDTO> findAllByBoardGroupAndDeleteFlagAndReply(BoardGroup boardGroup, String deleteFlag, Pageable pageable) {

        QueryResults<BoardListDTO> results = jpaQueryFactory
                                        .select(Projections.constructor(BoardListDTO.class,
                                                communityBoard.id,
                                                communityBoard.title,
                                                communityBoard.people.name,
                                                communityBoard.createdAt,
                                                communityBoard.viewCount,
                                                communityBoard.heart,
                                                communityBoardReply.countDistinct()))
                                        .from(communityBoard, communityBoardReply)
                                        .leftJoin(communityBoard.communityBoardReplies, communityBoardReply)
                                        .where(communityBoard.boardGroup.eq(boardGroup).and(communityBoard.deleteFlag.eq(deleteFlag)).and(communityBoardReply.deleteFlag.eq(deleteFlag))
                                                .or(communityBoard.boardGroup.eq(boardGroup).and(communityBoard.deleteFlag.eq(deleteFlag)).and(communityBoardReply.deleteFlag.isNull())))
                                        .groupBy(communityBoard)
                                        .offset(pageable.getOffset())
                                        .limit(pageable.getPageSize())
                                        /*.orderBy(communityBoard, communityBoard.createdAt.desc())*/
                                        .fetchResults();

   /*     Map<Integer, List<CommunityBoardReply>> results = jpaQueryFactory.from(communityBoard, communityBoardReply)
                .leftJoin(communityBoard.communityBoardReplies, communityBoardReply)
                .where(communityBoard.boardGroup.eq(boardGroup),
                        communityBoard.deleteFlag.eq(deleteFlag),
                        communityBoardReply.deleteFlag.eq(deleteFlag))
                .transform(groupBy(communityBoard.id).as(list(comment)));*/

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}