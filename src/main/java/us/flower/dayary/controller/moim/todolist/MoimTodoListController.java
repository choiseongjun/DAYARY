package us.flower.dayary.controller.moim.todolist;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONUtil;
import us.flower.dayary.common.MediaUtils;
import us.flower.dayary.domain.MoimBoard;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.ToDoWrite;
import us.flower.dayary.domain.ToDoWriteList;
import us.flower.dayary.domain.UploadFile;
import us.flower.dayary.domain.DTO.BaseResponse;
import us.flower.dayary.domain.DTO.MoimBoardAllDTO;
import us.flower.dayary.repository.moim.picture.MoimBoardFileRepository;
import us.flower.dayary.repository.moim.picture.MoimBoardRepository;
import us.flower.dayary.repository.moim.todo.ToDoWriteListRepository;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.service.moim.moimService;
import us.flower.dayary.service.moim.board.MoimBoardService;
import us.flower.dayary.service.moim.image.MoimImageService;
import us.flower.dayary.service.moim.todo.ToDoWriteService;


@Controller
public class MoimTodoListController {
	@Autowired 
	private ToDoWriteService service;
	@Autowired 
	private moimService moimService;
	@Autowired
	MoimBoardRepository moimboardRepository;
	@Autowired
	MoimBoardFileRepository moimboardfileRepostiory;
	@Autowired
	MoimImageService moimImageService;
	@Autowired
	private ToDoWriteListRepository toDoWriteListRepository;
	@Autowired
	MoimBoardService moimBoardService;
	
	 /**
     * 모임  해야할일(ToDoList) 현재목록  DetailView  조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimDetail/moimTodoList/moimtodostatus/moimtodostatusDetail/{no}")
    public String todostatusdetail(@PathVariable("no") long no,Model model) {
    	model.addAttribute("list",service.findByToDoWrite_id(no));
    	model.addAttribute("todo",service.findById(no));
    	return "moim/moimtodostatusDetail";
    }
    /**
     * 모임  해야할일(ToDoList) 완료된 것 저장
     *
     * @param 
     * @return
     * @throws 
     * @author jy
     */
    @ResponseBody
    @PostMapping("/moimDetail/moimTodoList/moimtodostatus/moimtodostatusDetail/{no}")
    public Map<String, Object> todostatusdetailpost(@PathVariable("no") long no,@RequestBody Map<String,String> param) {
    	Map<String, Object> returnData = new HashMap<String, Object>();
    		
    	 
		try {
			returnData.put("todo", service.updateList(param.get("list"),no,Integer.parseInt(param.get("count"))));
            returnData.put("code", "1");
            returnData.put("message", "저장되었습니다");

        } catch (Exception e) {
            returnData.put("code", "E3290");
            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
        }
    	      
    	  return returnData;
    }
    /**
     * 모임  해야할일(ToDoList) 불러와서 조회하기
     *
     * @param 
     * @return
     * @throws 
     * @author jy
     */
    @ResponseBody
    @GetMapping("/moimDetail/moimTodoList/detail/{no}")
    public Map<String,Object>  todostatdetail(@PathVariable("no") long no) {
    	
    	Map<String,Object> data=new HashMap<String,Object>();
    	data.put("list",service.findByToDoWrite_id(no));
    	try {
    	
    		ToDoWrite todo=service.findById(no);
    		data.put("todo",todo);
    		data.put("writer", todo.getPeople());
    		data.put("code", "1");
    		data.put("message", "저장되었습니다");

	        } catch (Exception e) {
	        	data.put("code", "E3290");
	        	data.put("message", "데이터 확인 후 다시 시도해주세요.");
	        }
    	
    	
    	return data;
    }
    /**
     * 모임  해야할일(ToDoList) 종료일자 수정
     *
     * @param 
     * @return
     * @throws 
     * @author jy
     */
    @ResponseBody
    @PostMapping("/moimDetail/moimTodoList/updateDetail")
    public Map<String,Object> update_date(@RequestBody ToDoWriteList toDoWriteList) {
    	Map<String,Object> data=new HashMap<String,Object>();
    	try {
    		data.put("todo",service.updateList(toDoWriteList));
    		//service.changeToDate(toDoWriteList.getToDoWrite());
    		data.put("code", "1");
    		data.put("message", "저장되었습니다");
    		
    	} catch (Exception e) {
    		data.put("code", "E3290");
    		data.put("message", "데이터 확인 후 다시 시도해주세요.");
    	}
    	
    	
    	return data;
    }

    /**
     * 모임 일정관리(ToDoList) status로 조회하기
     *
     * @param 
     * @return
     * @throws 
     * @author jy
     */
    @ResponseBody
    @GetMapping("/moimDetail/moimTodoList/{no}/{status}")
    public JSONObject  moimTodoListNew(@PathVariable("no") long no,@PathVariable("status") String status,@PageableDefault Pageable pageable ){
    	JSONObject returnData = new JSONObject();
    	int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
        pageable = PageRequest.of(page, 10,Sort.by("id").descending());
    //	pageable = PageRequest.of(1, 10, new Sort(Direction.DESC, "id"));
         try {
        	 if(status.indexOf(",")>0) {
        		 String[] x=status.split(",");
        		 if(x.length!=3) {
        			 returnData.put("todolist",service.findByMoim_idAndPeople_nameAndStatus(no,x[1],""));
        		 } else
        			 returnData.put("todolist",service.findByMoim_idAndPeople_nameAndStatus(no,x[1],x[2]));
        	 }else {
        		 Page<ToDoWrite> toDolist=service.findByMoim_idAndStatus(no,status,pageable);
        		 returnData.put("todolist",toDolist);
        		 
        	 }
        	 returnData.put("status",status);
	         returnData.put("code", "1");
         }catch(Exception e) {
        		returnData.put("message", e.getCause()+e.getMessage());
         }
	  return returnData;
    }
  
    /**
     * 모임 일정관리(ToDoList) 작성하기
     *
     * @param  
     * @return
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimDetail/moimTodoList/moimTodowrite/{no}")
    public String moimTodowrite(@PathVariable("no") long no,Model model) {
    		model.addAttribute("no",no);
    	
    		return "moim/moimTodowrite";
    }
    /**
     * 모달창 todowrite에 대한 상세설명 및 사진 작성하기
     *
     * @param locale
     * @param 
     * @return
     * @throws 
     * @author JY
     */
	@ResponseBody
	@PostMapping("/moimDetail/moimTodoList/modalWrite/{moimNo}/{no}")
	public Map<String, Object> modalWrite(HttpSession session,@RequestPart(name="File",required=false) MultipartFile[] file,@RequestPart(name="MoimBoard") MoimBoard board
			,@PathVariable("no")long no, @PathVariable("moimNo")long moimNo) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		String id =  (String) session.getAttribute("peopleEmail");
		
		  try {
			  	service.writeBoard(file,board,no,id, moimNo );
	            returnData.put("code", "1");
	            returnData.put("message", "저장되었습니다");

	        } catch (Exception e) {
	            returnData.put("code", "E3290");
	            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
	        }
	      
	  return returnData;
	}
	
	/**
	 * 모달창 todowrite에 대한 설명조회
	 *
	 * @param locale
	 * @param 
	 * @return
	 * @throws 
	 * @author JY
	 */
	@PostMapping("/moimDetail/moimTodoList/sidenav/{no}")
	public String modelView(@PathVariable("no") long no, Sort sort, Model model, Pageable pageable, HttpSession session) {

		sort = sort.and(new Sort(Sort.Direction.DESC, "id"));
		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
		pageable = PageRequest.of(page, 5, sort);
		Page<MoimBoard> pagelist = moimboardRepository.findBoardByToDoWriteList_id(pageable, no);
		
		long peopleid = (long) session.getAttribute("peopleId");
		
//		List<MoimBoard> list = moimboardRepository.findByToDoWriteList_id(no);
		ToDoWriteList todoList = toDoWriteListRepository.findById(no).get();
		session.setAttribute("peopleid", peopleid);
		model.addAttribute("todo", todoList);
		model.addAttribute("detailView", pagelist);
		model.addAttribute("moimid", todoList.getMoim().getId());

		return "moim/popup/sidenav";
	}

	/**
	 * 모달창 todowrite 게시판 글 수정
	 * @param id
	 * @return
	 * @author suyn
	 */
	@ResponseBody
	@PutMapping("/moimDetail/moimTodoList/updateModalBoard/{id}")
	public Map<String, Object> updateModalView(@PathVariable("id")long id,
//			@PathVariable("memo")String memo,
			@RequestBody MoimBoard moimBoard
			){
		moimBoard.setId(id);
		BaseResponse baseResponse = service.updateBoardById(moimBoard);

		Map<String, Object> returnData = new HashMap<String, Object>();

		returnData.put("code", baseResponse.getCode());
		returnData.put("message", baseResponse.getMessage());
		
		return returnData;
	}
	
	/**
	 * 모달창 todowrite 게시판 글 삭제
	 * @param id
	 * @return
	 * @author suyn
	 */
	@ResponseBody
	@DeleteMapping("/moimDetail/moimTodoList/deleteModalBoard/{id}")
	public Map<String, Object> deleteModalView(@PathVariable("id")long id){
		
		BaseResponse baseResponse = service.deleteBoardById(id);
		Map<String, Object> returnData = new HashMap<String, Object>();
		
		returnData.put("code", baseResponse.getCode());
		returnData.put("message", baseResponse.getMessage());
		
		return returnData;
	}
	
	@ResponseBody
	@PostMapping("/moimDetail/moimTodoList/image")
	public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file){
		try {
			UploadFile uploadFile = moimImageService.store(file);
			return ResponseEntity.ok().body("/moimDetail/moimTodoList/image/"+uploadFile.getId());
		}catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
	
	@GetMapping("/moimDetail/moimTodoList/image/{fileId}")
	@ResponseBody
	public ResponseEntity<?> serveFile(@PathVariable long fileId) {
		try {
			UploadFile uploadedFile = moimImageService.load(fileId);
			HttpHeaders headers = new HttpHeaders();

			Resource resource = moimImageService.loadAsResource(uploadedFile.getSaveFileName());
			String fileName = uploadedFile.getFileName();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

			if (MediaUtils.containsImageMediaType(uploadedFile.getContentType())) {
				headers.setContentType(MediaType.valueOf(uploadedFile.getContentType()));
			} else {
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			}

			return ResponseEntity.ok().headers(headers).body(resource);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
	
	/**
	 * 모임 일정관리(ToDoList) 작성하기
	 *
	 * @param locale
	 * @param ToDoWriteList
	 * @return
	 * @throws 
	 * @author JY
	 */
	@ResponseBody
	@PostMapping("/moimDetail/moimTodoList/moimTodowrite/{no}")
	public Map<String, Object> moimTodowrite(HttpSession session,@RequestBody ToDoWriteList todo ,@PathVariable("no") long no) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		String id =  (String) session.getAttribute("peopleEmail");
			
		try {
			if(id=="") {
				returnData.put("message","로그인해주세요");
				throw new Exception("로그인해주세요");
			}
			service.saveList(todo,id,no);
			returnData.put("code", "1");
			returnData.put("message", "저장되었습니다");
			
		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}
		
		return returnData;
	}
    /**
     * 모임 해야할일(ToDoList)에서 달력  조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimDetail/moimTodoList/moimcalender")
    public String moimcalender() {
    	
    	return "moim/moimCalender";
    }
    /**
     * 모임 해야할일(ToDoList) 현재목록  조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
//    @GetMapping("/moimDetail/moimTodoList/moimtodostatus/{no}")
//    public String moimtodostatus(@PathVariable("no") long no,Model model) {
//    	
//    	model.addAttribute("no",no);
//    	model.addAttribute("list", service.findByMoim_id(no));
//    	
//    	return "moim/moimTodostatus";
//    }
	   /**
     * 모임 해야할일(ToDoList) 목록 조회
     *
     * @param 
     * @return
	 * @throws IOException 
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimDetail/moimTodoList/{no}")
    public String moimTodoList(@PathVariable("no") long no,Model model,@PageableDefault Pageable pageable,HttpSession session
    		,HttpServletResponse response) throws IOException {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
        pageable = PageRequest.of(page, 9,Sort.by("id").descending());
    	Page<ToDoWrite> toDolist=service.findByMoim_id(pageable,no);
    	boolean moim=service.existByMoim_idAndPeople_idAndMoimpeopleJoinCondition(no,(long)session.getAttribute("peopleId"),'Y');
    	if(moim==false) {
    		response.setContentType("text/html; charset=UTF-8");
    		 
    		PrintWriter out = response.getWriter();
    		 
    		out.println("<script>alert('모임에 가입하여 주세요'); location.href='/moimlistView/moimdetailView/"+no+"';</script>");
    		 
    		out.flush();

    	}
    	model.addAttribute("moim",moimService.findMoimone(no).get());//이쿼리문 수정해야함 모든 내부데이터 다들고옴 20200227 최성준
    	model.addAttribute("todolist", toDolist);
    	model.addAttribute("moimPeople",Boolean.toString(moim));
    	model.addAttribute("count",service.countByMoim_idAndStatus(no));
    	model.addAttribute("status","allList");
    	model.addAttribute("moimNo",no);
    	return "moim/moimTodoList";
    }
    /**
     * 내가 작성한 해야할일(ToDoList) 목록 조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimDetail/moimTodoList/myList/{no}")
    public String myTodoList(@PathVariable("no") long no,Model model,@PageableDefault Pageable pageable,HttpSession session) {
    	
    	int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
    	pageable = PageRequest.of(page, 9,Sort.by("id").descending());
    	long people=(long)session.getAttribute("peopleId");
    	
    	Page<ToDoWrite> toDolist=service.findByMoim_idAndPeople_id(pageable,no,people);
    	
    	boolean moim=service.existByMoim_idAndPeople_id(no,people);
    	model.addAttribute("moim",moimService.findMoimone(no).get());
    	model.addAttribute("todolist", toDolist);
    	model.addAttribute("moimPeople",Boolean.toString(moim));
    	model.addAttribute("count",service.countByMoim_idAndStatus(no));
    	model.addAttribute("status","myList");
    	return "moim/moimTodoList" ;
    }
    
// 타임리프 형식
    @GetMapping("/moimDetail/moimTodoList/boardTimeline/{no}")
    public String boardTimeline(@PathVariable("no") long no, Model model, @PageableDefault Pageable pageable, Sort sort) {
    	
    	int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
    	pageable = PageRequest.of(page, 3, Sort.by("id").descending());
    	
    	Page<MoimBoard> boardList = moimBoardService.getMoimBoardByMoimIdPaging(pageable, no);
    	model.addAttribute("boardList", boardList);
    	model.addAttribute("moimId", no);
    	System.out.println(">>> "+ boardList.toString());
    	
    	return "moim/moimBoardTimeline";
    }
    
    
    @ResponseBody
    @GetMapping("/moimDetail/moimTodoList/boardTimelineMore/{id}")
    public Map<String, Object> boardTimelineMore(@PathVariable("id") long id, @PageableDefault Pageable pageable, Sort sort) {
    	
		Map<String, Object> returnData = new HashMap<String, Object>();

		try {
			List<MoimBoardAllDTO> boardList = moimBoardService.getMoimBoardByMoimId(id);
			
			returnData.put("code", "1");
			returnData.put("message", "성공");
			returnData.put("boardList", boardList);
			
		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");	
		}
		
    	return returnData;
    }
    
  @GetMapping("/moim/boardTimeline")
  public String boardTimeline() {
  	
  	return "moim/moimBoardTimeline";
  }
    
    /**
     * todo 삭제
    *
    * @param 
    * @return
    * @throws 
    * @author JY
    * @date 2019-10-04
    */
   @ResponseBody
   @DeleteMapping("/moimDetail/moimTodoList/delete/{no}")
   public Map<String, Object> MoimDeleteOne(@PathVariable("no") long no) {
   
      Map<String, Object> returnData = new HashMap<String, Object>();
      
      try {
    	   service.deleteById(no);	  
           returnData.put("code", "1");
           returnData.put("message", "삭제되었습니다");

       } catch (Exception e) {
           returnData.put("code", "E3290");
           returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
       }


      return returnData;
   }
   
}
