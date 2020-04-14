package us.flower.dayary.service.moim.todo;




import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import us.flower.dayary.domain.CommunityBoard;
import us.flower.dayary.domain.CommunityFile;
import us.flower.dayary.domain.MoimBoard;
import us.flower.dayary.domain.MoimBoardFile;
import us.flower.dayary.domain.ToDoWrite;
import us.flower.dayary.domain.ToDoWriteList;
import us.flower.dayary.domain.DTO.BaseResponse;
public interface ToDoWriteService {

	public void saveList(ToDoWriteList toDoWriteList,String id,long no);
	public Page<ToDoWrite> findByMoim_id(Pageable page,long id); 
	public Page<ToDoWrite> findByMoim_idAndPeople_id(Pageable page,long id,long people); 
	//public List<ToDoWrite> findByMoim_id(long id); 
	public List<ToDoWriteList> findByToDoWrite_id(long id);
	public ToDoWrite findById(long id);
	public void updateList(String list,long no,int count);
	public boolean existByMoim_idAndPeople_id(long id,long peopleId);
	//public void updateById(long id,Date date);
	public void deleteById(long id);
	 public Page<ToDoWrite> findByMoim_idAndStatus(long id, String status, Pageable pageable);
	 public List<ToDoWrite> findByMoim_idAndPeople_nameAndStatus(long id, String name,String status);
	 public int[] countByMoim_idAndStatus(long id);
	 public void writeBoard(MultipartFile[] file,MoimBoard board,long no,String id);
	 public void changeToDate(ToDoWrite todo);
	 public List<MoimBoardFile> findByToDoWriteList_id(long id);
	 public void updateById(java.util.Date date);
	 public void saveListTodoRest(ToDoWriteList todo, long peopleNo, long no);//Rest풀(리액트네이티브) 일정작성
	 
	 public BaseResponse deleteBoardById(long boardId);
	 public BaseResponse updateBoardById(MoimBoard moimBoard);
	 
	 }
