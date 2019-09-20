package us.flower.dayary.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.People;
import us.flower.dayary.repository.MoimPeopleRepository;
import us.flower.dayary.repository.MoimRepository;
import us.flower.dayary.service.moimService;



@Controller
public class MoimController {

    @Autowired
    private moimService moimService;

    @Autowired
    MoimPeopleRepository moimpeopleRepository;
	@Autowired
	MoimRepository moimRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(MoimController.class);
 
    /**
     * 모임 삭제
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     * @date 2019-09-20
     */
	@ResponseBody
	@DeleteMapping("/moimDetail/moimDeleteOne/{moimNo}")
	public Map<String, Object> MoimDeleteOne(@PathVariable("moimNo") long moimNo,Model model,Sort sort) {
	
		Map<String, Object> returnData = new HashMap<String, Object>();
		
		try {
			moimService.deleteMoimOne(moimNo);

		  	returnData.put("code", "1");
            returnData.put("message", "삭제되었습니다");

        } catch (Exception e) {
            returnData.put("code", "E3290");
            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
        }
 

		return returnData;
	}
    
    /**
     * 모임  디테일 게시판(공지사항,가입인사 및 자기소개,자유게시판 등등)  조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimdetailView/moimboard/{no}")
    public String moimboard(@PathVariable("no") long no) {
    	
    	
    	return "moim/moimDetailboard";
    }
    
    /**
     * 모임  해야할일(ToDoList) 현재목록  DetailView  조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimDetail/moimTodoList/moimtodostatus/{no}/moimtodostatusDetail")
    public String todostatusdetail(@PathVariable("no") long no) {
    	
    	return "moim/moimtodostatusDetail";
    }
    /**
     * 모임 일정관리(ToDoList) 완료된것만 보기
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimDetail/moimTodoList/moimTodoListcompleted/{no}")
    public String moimTodoListcompleted(@PathVariable("no") long no) {
    	 
    	return "moim/moimTodoListcompleted";
    }
    /**
     * 모임 일정관리(ToDoList) 작성하기
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimDetail/moimTodoList/moimTodowrite")
    public String moimTodowrite() {
    	 
    	return "moim/moimTodowrite";
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
    @GetMapping("/moimDetail/moimTodoList/moimtodostatus/{no}")
    public String moimtodostatus(@PathVariable("no") long no,Model model) {
    	
    	model.addAttribute("no",no);
    	
    	return "moim/moimTodostatus";
    }
    /**
     * 모임 단체채팅방 조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun 
     */
    @GetMapping("/moimDetail/moimChatroom/{no}")
    public String moimChatroom(@PathVariable("no") long no,Model model) {
    	Optional<Moim> moimOne=moimRepository.findById(no);
        List<People> moimpeopleList=moimOne.get().getPeopleList();
        
        
        model.addAttribute("moimpeopleList",moimpeopleList);
        model.addAttribute("moimOne",moimOne); 
        System.out.println(moimOne.toString());
    	return "moim/moimChatroom";
    }
    /**
     * 모임 사진첩 글쓰기
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun 
     */
    @GetMapping("/moimDetail/{no}/moimPicture/moimPictureWrite")
    public String moimPictureWrite(@PathVariable("no") long no,Model model) {
    	
    	model.addAttribute("no",no);
    	
    	return "moim/moimpictureWrite"; 
    }
    /**
     * 모임 사진첩 조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun 
     */
    @GetMapping("/moimDetail/{no}/moimPicture")
    public String moimPicture(@PathVariable("no") long no,Model model) {
    	
    	model.addAttribute("no",no);
    	
    	return "moim/moimpictureList"; 
    }
    /**
     * 모임 해야할일(ToDoList) 목록 조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
    @GetMapping("/moimDetail/moimTodoList/{no}")
    public String moimTodoList(@PathVariable("no") long no,Model model) {
    	
    	
    	model.addAttribute("no",no);
    	return "moim/moimTodoList";
    }
    /**
     * 모임 카테고리 목록 조회
     *
     * @param locale
     * @param Moim
     * @return
     * @throws 
     * @author yuna
     */
    @ResponseBody
    @PostMapping("/getMoimCategory")
    public Map<String, Object> getMoimCategory(){
    	
    	Map<String, Object> categoryList = new HashMap<String, Object>();
    	try {
    		categoryList =  moimService.getMoimCategory();
    		
    	}catch (Exception e) {
		}
    	return categoryList;
    }

    /**
     * 모임 만들기
     *
     * @param locale
     * @param Moim
     * @return
     * @throws 
     * @author yuna
     */
    @ResponseBody
    @PostMapping("/moimMake")
    public Map<String, Object> moimMake(@RequestPart("moim") Moim moim, @RequestPart("file") MultipartFile file, HttpSession session) {

        Map<String, Object> returnData = new HashMap<String, Object>();
        String id = (String) session.getAttribute("peopleEmail");
        String subject = moim.getCategory().getSubject();
        
        if (id.equals(null) || id.equals("")) {
            returnData.put("code", "0");
            returnData.put("message", "로그인 후 이용해주세요");
            return returnData;
        } else if (moim.getTitle().equals(null) || moim.getTitle().equals("")) {
            returnData.put("code", "0");
            returnData.put("message", "모임 이름을 입력해주세요");
            return returnData;
        } else if (moim.getPeopleLimit() == 0) {
            returnData.put("code", "0");
            returnData.put("message", "인원수를 입력해주세요");
            return returnData;
        } else if (subject == null || subject.equals("")) {
            returnData.put("code", "0");
            returnData.put("message", "모임 주제를 선택해주세요");
            return returnData;
        }

        try {
            moimService.saveMoim(id, subject, moim, file);
            returnData.put("code", "1");
            returnData.put("message", "저장되었습니다");

        } catch (Exception e) {
            returnData.put("code", "E3290");
            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
        }
        return returnData;
    }

    /**
     * 모임 디테일 출력
     *
     * @param locale
     * @param no
     * @return moimDetail,people_no
     * @throws Exception
     * @author choiseongjun
     */
    @GetMapping("/moimlistView/moimdetailView/{no}")
    public String moimDetailView(@PathVariable("no") long no, Model model,HttpSession session) {
      
    	
    	
        moimService.findMoimone(no).ifPresent(moimDetail -> model.addAttribute("moimDetail", moimDetail));
        
        Long people_no = (Long) session.getAttribute("peopleId");//일반회원 번호를 던져준다.참가를 위해 
        session.setAttribute("people_no", people_no);

       
        
        Optional<Moim> moimOne=moimRepository.findById(no);
        List<People> moimpeopleList=moimOne.get().getPeopleList();
        System.out.println("======================");
        System.out.println(moimOne.toString());
        System.out.println("======================"); 
        System.out.println(moimpeopleList.toString());
        System.out.println("======================");
        
//        long checkPeople=moimpeopleRepository.countBypeopleNo(people_no);//모임참가회원인지 체크하는것
        
        
        model.addAttribute("no",no);
        model.addAttribute("moimOne",moimOne);
        
        return "moim/moimDetail"; 
    }

    /**
	 * 모임 리스트 출력(Paging 처리)
	 *
	 * @param locale
	 * @param Moim
	 * @return moimList
	 * @throws Exception
	 * @author choiseongjun
	 */
	@GetMapping("/moimlistView")
	public String moimListView(@PageableDefault Pageable pageable,HttpSession session,Model model) {

		 int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
	        pageable = PageRequest.of(page, 9);
	        
		Page<Moim> moimList = moimRepository.findAll(pageable);
		model.addAttribute("moimList", moimList);
	
		logger.info(moimList.toString());
		return "moim/moimList";
	}
	/**
	 * 일반회원 모임 참가 
	 *
	 * @param locale
	 * @param Moim
	 * @return returnData
	 * @throws Exception
	 * @author choiseongjun
	 */
	@ResponseBody
	@PostMapping("/moimParticipant/{moimNo}")
	public Map<String, Object> moimParticipant(@PathVariable("moimNo") long moimNo,HttpSession session) {
		Long peopleId = (Long) session.getAttribute("peopleId");
		
		System.out.println(peopleId);
		Map<String,Object> returnData = new HashMap<String,Object>();
		returnData.put("code","1");
		returnData.put("message","모임가입완료:)");
		moimService.moimParticipant(peopleId,moimNo);
		return returnData;
	}
	
	@ResponseBody
    @GetMapping("/getMoimImage/{imageName:.+}")
    public byte[] getMoimImage(@PathVariable("imageName") String imageName) throws Exception {
        return moimService.getMoimImage(imageName);
    }

    @GetMapping("/moimMakeView")
    public String moimMakeView() {
        return "moim/moimMake";
    }

}
