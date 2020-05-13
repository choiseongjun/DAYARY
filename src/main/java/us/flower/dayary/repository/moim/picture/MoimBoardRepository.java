package us.flower.dayary.repository.moim.picture;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import us.flower.dayary.domain.BoardGroup;
import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimBoard;
import us.flower.dayary.domain.ToDoWriteList;
import us.flower.dayary.domain.DTO.MoimBoardListDTO;


public interface MoimBoardRepository extends JpaRepository<MoimBoard, Long>, MoimBoardRepositoryCustom {

	List<MoimBoard> findByToDoWriteList_id(long id);
	Page<MoimBoard> findBoardByToDoWriteList_id(Pageable page, long id);

	void deleteByToDoWriteList_id(long id);
 
	@Query(value = "select m from MoimBoard m inner join  m.moimBoardfile f WHERE m.moim=(:moim) and  m.boardGroup=(:boardGroup) and f.representImage = (:representImage)",countQuery = "select count(m) from MoimBoard m")
	List<MoimBoard> searchRepresent(@Param("boardGroup") BoardGroup boardGroup,@Param("moim") Moim moim,@Param("representImage") long representImage);

	//public Page<MoimBoardImage> search(final Pageable pageable);

	List<MoimBoard> findByBoardGroup(BoardGroup board_group_id);

	MoimBoard findById(long id);
	
	//Page<MoimBoard> findByboardGroup_idAndMoim_id(long l, Moim moim, Pageable page);

	List<MoimBoard> findByboardGroup_idAndMoim_id(long l, long no);

	Page<MoimBoard> findByMoim_id(Moim moim, Pageable page);
	@Modifying
	@Transactional
	@Query("UPDATE MoimBoard SET delete_flag='Y' WHERE toDoWriteList = (:todoWriteList)")
	void updateDeleteYn(@Param("todoWriteList") ToDoWriteList todoWriteList);

	//Page<MoimBoardListDTO> findByBoardGroup(Long boardGroupId, Pageable pageable);

	List<MoimBoard> findAllByMoimAndBoardGroupOrderById(Moim moim, BoardGroup boardGroup);
	
//	@Modifying
//	@Transactional
//	@Query("UPDATE MoimBoard m SET m.memo=memo WHERE m.id = :id")
//	void updateBoardMemo(@Param("id")long id, @Param("memo")String memo);

	List<MoimBoard> findByMoim_id(long id);
	Page<MoimBoard> findByMoim_id(long id, Pageable page);
}
