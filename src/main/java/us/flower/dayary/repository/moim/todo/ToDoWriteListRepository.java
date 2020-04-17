package us.flower.dayary.repository.moim.todo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import us.flower.dayary.domain.ToDoWrite;
import us.flower.dayary.domain.ToDoWriteList;

public interface ToDoWriteListRepository extends JpaRepository<ToDoWriteList, Long>{
	
	//todo세부 갯수
	int countByToDoWrite_id(long id);
	int countByCheckConfirmAndToDoWrite_id(char Y,long id);
	List<ToDoWriteList> findByToDoWrite_id(long id);
	List<ToDoWriteList> findByMoim_id(long id);
	List<ToDoWriteList> findByMoim_idOrderByIdDesc(long id);
	void deleteByToDoWrite_id(long id);
	@Modifying
	@Transactional
	@Query("UPDATE ToDoWriteList SET delete_flag='Y' WHERE toDoWrite = (:todowrite)")
	void updateToDoWrite_id(@Param("todowrite") ToDoWrite todowrite);
}
