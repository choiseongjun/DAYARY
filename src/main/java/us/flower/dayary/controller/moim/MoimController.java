package us.flower.dayary.controller.moim;

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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.minidev.json.JSONObject;
import us.flower.dayary.domain.Common;
import us.flower.dayary.domain.Meetup;
import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.ToDoWrite;
import us.flower.dayary.repository.chat.MoimChatRepository;
import us.flower.dayary.repository.moim.MoimPeopleRepository;
import us.flower.dayary.repository.moim.MoimRepository;
import us.flower.dayary.repository.moim.meetup.MoimMeetUpRepository;
import us.flower.dayary.repository.moim.picture.MoimBoardFileRepository;
import us.flower.dayary.repository.moim.todo.ToDoWriteRepository;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.service.moim.moimService;

@Controller
public class MoimController {

	@Autowired
	private moimService moimService;

	@Autowired
	private MoimPeopleRepository moimpeopleRepository;
	@Autowired
	private MoimRepository moimRepository;
	@Autowired
	private PeopleRepository peopleRepository;

	@Autowired
	private ToDoWriteRepository toDowriteRepository;

	@Autowired
	private MoimMeetUpRepository moimmeetupRepository;

	@Autowired
	private MoimChatRepository moimchatRepository;
	@Autowired
	private MoimBoardFileRepository moimboardfileRepository;
	private static final Logger logger = LoggerFactory.getLogger(MoimController.class);

//    @GetMapping("/searchTitle")
//	public String listUsers(Model model, @RequestParam(defaultValue="")  String name) {
//		
//    	System.out.println(name);
//    	model.addAttribute("moimsearchList", moimService.findByTitle(name));
//		return "moim/moimList";
//	}

	/**
	 * 모임 삭제
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 * @date 2019-09-20
	 */
	@ResponseBody
	@DeleteMapping("/moimDetail/moimDeleteOne/{moimNo}")
	public Map<String, Object> MoimDeleteOne(@PathVariable("moimNo") long moimNo, Model model, Sort sort) {

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
	 * 모임 생성에 필요한 목록들 조회
	 *
	 * @param
	 * @return
	 * @author yuna
	 * @author hyoz 수정
	 */
	@ResponseBody
	@GetMapping("/moimelemtent")
	public Map<String, Object> getMoimElement() {
		/*
		Map<String, Object> list = new HashMap<String, Object>();
		try {
			// categoryList = moimService.getMoimCategory();
			list = moimService.getMoimElement(option);
		} catch (Exception e) {
		}
		*/

		return moimService.getMoimElement();
	}

	/**
	 * 모임 만들기
	 *
	 * @param moim
	 * @return
	 * @throws @author yuna
	 */
	
	@ResponseBody
	@PostMapping("/moimMake")
	public Map<String, Object> moimMake(@RequestPart("moim") Moim moim,
			@RequestPart(name = "file", required = false) MultipartFile file, HttpSession session) {
		Map<String, Object> returnData = new HashMap<String, Object>();

		String id = (String) session.getAttribute("peopleEmail");
		Long peopleId = (Long) session.getAttribute("peopleId");
		String subject = moim.getCategory().getCommName();
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
		} else if (moim.getCategory() == null || moim.getCategory().equals("")) {
			returnData.put("code", "0");
			returnData.put("message", "모임 주제를 선택해주세요");
			return returnData;
		} else if (moim.getSidocode() == null || moim.getSidocode().equals("") || moim.getSigooncode() == null
				|| moim.getSigooncode().equals("")) {
			returnData.put("code", "0");
			returnData.put("message", "활동 지역을 선택해주세요");
			return returnData;
		}else if(moim.getRecruitStatus() == null || moim.getRecruitStatus().equals("") ) {
			returnData.put("code", "0");
			returnData.put("message", "모임 상태를 선택해주세요");
			return returnData;
		}
		char joinCondition='Y';//참가자 승인후 Y Defualt Value
		try {
			moimService.saveMoim(id, subject, moim, file);
			
			long MoimId = moimService.selectMaxMoimId();
			char maker='Y';//만든사람여부
			//모임참가자 서비스를 들고와서 재사용한다(모임참가자테이블에도넣기위함 알림을 모임참가자테이블로 보내기에 모임장도 넣어야한다 ) by choiseongjun 20191221
			moimService.moimParticipant(peopleId,MoimId,joinCondition,maker);
			returnData.put("code", "1");
			returnData.put("message", "저장되었습니다");

		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}
		return returnData;
	}
	/**
	 * 계획 상태 비율 원차트
	 *
	 * @param locale
	 * @param no
	 * @return moimDetail,people_no
	 * @throws Exception
	 * @author choiseongjun
	 */

	
	@ResponseBody
	@GetMapping("/TodoStatusChart/{no}")
	public JSONObject TodoStatusChart(@PathVariable("no") long no) {
		JSONObject returnData = new JSONObject();
		
		List<ToDoWrite> StachartList = moimService.selectTodoLankChart(no);//계획리스트 그상태별 차트리스트
		returnData.put("StachartList",StachartList);
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
	public String moimDetailView(@PathVariable("no") long no, Model model, HttpSession session, Sort sort,
			@PageableDefault Pageable pageable,Authentication authentication) {
		
		
		String username = authentication.getName();
		People user = peopleRepository.findByName(username);
		moimService.findMoimone(no).ifPresent(moimDetail -> model.addAttribute("moimDetail", moimDetail));// 모임장중심으로 데이터
																								// 불러옴
		long peopleId = user.getId();
		session.setAttribute("peopleId", peopleId);

		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
		pageable = PageRequest.of(page, 2, Sort.Direction.DESC, "id");// 내림차순으로 정렬한다

		String moimPeopleNo = moimService.findMoimPeopleNoOne(peopleId, no);// 참여자단건 조회(모임피플넘버를 단건으로 가져와서 moimPeople_no에
																			// 넣어준다)
		List<MoimPeople> joinedpeoplelist = moimpeopleRepository.findByMoim_idAndPeople_idAndMaker(no, peopleId,'N');// 현재 접속한 유저
																											// 리스트를 들고옴

		List<MoimPeople> joinedmoimpeopleList = moimpeopleRepository.findByMoim_idAndMaker(no,'N');// 이건 모임내 전체사람조회,모임장제외해서 들고옴
		for (int i = 0; i < joinedpeoplelist.size(); i++) {
			long joinedpeople = joinedpeoplelist.get(i).getId();
			model.addAttribute("joinedpeople", joinedpeople);
		}
		Optional<Moim> moimOne = moimRepository.findById(no);
		//List<People> moimpeopleList = moimOne.get().getPeopleList();
		List<MoimPeople> moimpeopleList = moimpeopleRepository.findByMoim_idAndMaker(no,'N');
		sort = sort.and(new Sort(Sort.Direction.DESC, "id"));
		List<Meetup> meetupList = moimmeetupRepository.findByMoim_id(no, pageable);// 오프라인 모임 내림차순정렬로 가져옴

		long chatcount = moimchatRepository.countByMoim_id(no);

		long picturecount = moimboardfileRepository.picturecount(no);

		long totalPeople = 0;
		for (int i = 0; i <= moimpeopleList.size(); i++) {// 데이터 값 들고온것을 size만큼 반복해서 뽑기 모임리스트까지 <=한 이유는 모임장이 제외됬기때문에
															// +1해야한다
			totalPeople++;
		}
		model.addAttribute("moimPeopleNo", moimPeopleNo);
		model.addAttribute("no", no);
		model.addAttribute("moimOne", moimOne);
		model.addAttribute("moimpeopleList", moimpeopleList);
		model.addAttribute("todoCount", toDowriteRepository.countByMoim_id(no));
		model.addAttribute("totalPeople", totalPeople);// 해당하는 모임의 총회원수 뽑기
		model.addAttribute("joinedpeoplelist", joinedpeoplelist);// 현재 접속한 유저(모임피플) 정보.
		model.addAttribute("joinedmoimpeopleList", joinedmoimpeopleList);// 모임가입된사람전체조회
		model.addAttribute("meetupList", meetupList);
		model.addAttribute("chatcount", chatcount);
		model.addAttribute("picturecount", picturecount);
		//model.addAttribute("boardGroup", "8L"); // 공지사항 게시판
		
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

	@GetMapping("/moimlistView/{commCode}")
	public String moimListView(@PageableDefault Pageable pageable, HttpSession session, Model model,
	@RequestParam(required = false) String title, @PathVariable("commCode") String commCode,String status,
	@RequestParam(required = false) String sido_code, @RequestParam(required = false) String sigoon_code) {

		
		
		
	int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
	pageable = PageRequest.of(page, 9, Sort.Direction.DESC, "id");// 내림차순으로 정렬한다

	// [hyozkim] 카테고리 데이터 추가
	List<Common> categories = (List<Common>) moimService.getMoimElement().get("element_CA1");
	if(commCode.equals("11")) {
		if (title != null || sido_code != null || sigoon_code != null || status != null) {
			
			if(title==null) {
				title="";
			}
			
			Page<Moim> moimList = moimService.selecttitleList(pageable, title, sido_code, sigoon_code);// 조건을 받아서 출력한다
			for(int i=0;i<moimList.getNumberOfElements();i++) {
				
				List<ToDoWrite> todowrite =  moimList.getContent().get(i).getTodowrite();
				double progresstotalSum=0;
				double progresstotal=0;
				double progressbefore = 0;
				double progress = 0;
				long count = 0;
			
				for(ToDoWrite j: todowrite) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("progress_done", j.getProgress_done());
					tempMap.put("sametodoid",j.getMoim().getId());
					progresstotalSum+=j.getProgress_done();
					progresstotal+=j.getProgress_total();
					progressbefore += j.getProgress();
					count++;
				}
				if(count==0) {
				count = 0;
				}
				
					double progressPercent=0;
					//progress = Double.parseDouble(String.format("%.2f",progressbefore / count));
					progressPercent = Math.round(((progresstotalSum/progresstotal)*100)*100)/100.0;
					moimList.getContent().get(i).setProgresssum((long)progresstotalSum);
					moimList.getContent().get(i).setProgresstotal((long)progresstotal);
					moimList.getContent().get(i).setProgresspercent(progressPercent);
					moimList.getContent().get(i).setTodocount(count);
				}
			
			model.addAttribute("moimList", moimList);
			model.addAttribute("title",title);
			model.addAttribute("sido_code",sido_code);
			model.addAttribute("sigoon_code",sigoon_code);
			model.addAttribute("status",status);
			long moimListcount = moimList.getTotalElements();
			model.addAttribute("moimListcount", moimListcount);
			
		
			
		}else {
			Page<Moim> moimList = moimService.selectMoimAll(pageable);// 모든 모임리스트 출력한다
			for(int i=0;i<moimList.getNumberOfElements();i++) {
				
				List<ToDoWrite> todowrite =  moimList.getContent().get(i).getTodowrite();
				double progresstotalSum=0;
				double progresstotal=0;
				double progressbefore = 0;
				double progress = 0;
				long count = 0;
			
				for(ToDoWrite j: todowrite) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("progress_done", j.getProgress_done());
					tempMap.put("sametodoid",j.getMoim().getId());
					progresstotalSum+=j.getProgress_done();
					progresstotal+=j.getProgress_total();
					progressbefore += j.getProgress();
					count++;
				}
				if(count==0) {
				count = 0;
				}
				
					double progressPercent=0;
					//progress = Double.parseDouble(String.format("%.2f",progressbefore / count));
					progressPercent = Math.round(((progresstotalSum/progresstotal)*100)*100)/100.0;
					moimList.getContent().get(i).setProgresssum((long)progresstotalSum);
					moimList.getContent().get(i).setProgresstotal((long)progresstotal);
					moimList.getContent().get(i).setProgresspercent(progressPercent);
					moimList.getContent().get(i).setTodocount(count);
				}
			
			model.addAttribute("moimList", moimList);
			long moimListcount = moimList.getTotalElements();
			model.addAttribute("moimListcount", moimListcount);
		}
		
	}else {
		if (title != null || sido_code != null || sigoon_code != null || status != null) {
			
			if(title==null) {
				title="";
			}
			Page<Moim> moimList = moimService.selectseacrhList(pageable, title, sido_code, sigoon_code,commCode);// 조건을 받아서 출력한다
			
			for(int i=0;i<moimList.getNumberOfElements();i++) {
				
				List<ToDoWrite> todowrite =  moimList.getContent().get(i).getTodowrite();
				double progresstotalSum=0;
				double progresstotal=0;
				double progressbefore = 0;
				double progress = 0;
				long count = 0;
			
				for(ToDoWrite j: todowrite) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("progress_done", j.getProgress_done());
					tempMap.put("sametodoid",j.getMoim().getId());
					progresstotalSum+=j.getProgress_done();
					progresstotal+=j.getProgress_total();
					progressbefore += j.getProgress();
					count++;
				}
				if(count==0) {
				count = 0;
				}
				
					double progressPercent=0;
					//progress = Double.parseDouble(String.format("%.2f",progressbefore / count));
					progressPercent = Math.round(((progresstotalSum/progresstotal)*100)*100)/100.0;
					moimList.getContent().get(i).setProgresssum((long)progresstotalSum);
					moimList.getContent().get(i).setProgresstotal((long)progresstotal);
					moimList.getContent().get(i).setProgresspercent(progressPercent);
					moimList.getContent().get(i).setTodocount(count);
				}
			
			model.addAttribute("moimList", moimList);
			model.addAttribute("title",title);
			model.addAttribute("sido_code",sido_code);
			model.addAttribute("sigoon_code",sigoon_code);
			model.addAttribute("status",status);
			
			long moimListcount = moimList.getTotalElements();
			model.addAttribute("moimListcount", moimListcount);
		
			
		}else { 
			Page<Moim> moimList = moimService.selectMoimCate(pageable,commCode);// 카테고리별로 모임리스트 출력한다
			
			for(int i=0;i<moimList.getNumberOfElements();i++) {
			
				List<ToDoWrite> todowrite =  moimList.getContent().get(i).getTodowrite();
				double progresstotalSum=0;
				double progresstotal=0;
				double progressbefore = 0;
				double progress = 0;
				long count = 0;
			
				for(ToDoWrite j: todowrite) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("progress_done", j.getProgress_done());
					tempMap.put("sametodoid",j.getMoim().getId());
					progresstotalSum+=j.getProgress_done();
					progresstotal+=j.getProgress_total();
					progressbefore += j.getProgress();
					count++;
				}
				if(count==0) {
				count = 0;
				}
				
					double progressPercent=0;
					//progress = Double.parseDouble(String.format("%.2f",progressbefore / count));
					progressPercent = Math.round(((progresstotalSum/progresstotal)*100)*100)/100.0;
					moimList.getContent().get(i).setProgresssum((long)progresstotalSum);
					moimList.getContent().get(i).setProgresstotal((long)progresstotal);
					moimList.getContent().get(i).setProgresspercent(progressPercent);
					moimList.getContent().get(i).setTodocount(count);
				}
			
			model.addAttribute("moimList", moimList);
			long moimListcount = moimList.getTotalElements();
			model.addAttribute("moimListcount", moimListcount);
		}
	}
	model.addAttribute("categories", categories);
//	if (title != null ||  sido_code != null || sigoon_code != null) {
//		Page<Moim> moimList = moimService.selecttitleList(pageable, title, sido_code, sigoon_code);// 타이틀을
//		
//		for(int i=0;i<moimList.getNumberOfElements();i++) {
//		List<ToDoWrite> todowrite =  moimList.getContent().get(i).getTodowrite();
//		double progresstotalSum=0;
//		double progresstotal=0;
//		double progressbefore = 0;
//		double progress = 0;
//		long count = 0;
//		for(ToDoWrite j: todowrite) {
//			Map<String, Object> tempMap = new HashMap<String, Object>();
//			tempMap.put("progress_done", j.getProgress_done());
//			tempMap.put("sametodoid",j.getMoim().getId());
//			progresstotalSum+=j.getProgress_done();
//			progresstotal+=j.getProgress_total();
//			progressbefore += j.getProgress();
//			count++;
//		}
//
//		if(count==0) {
//		count = 1;
//		}
//
//	//progress = Double.parseDouble(String.format("%.2f",progressbefore / count));
//
//		double progressPercent=0;
//
//		progressPercent = Math.round(((progresstotalSum/progresstotal)*100)*100)/100.0;
//		moimList.getContent().get(i).setProgresssum((long)progresstotalSum);
//		moimList.getContent().get(i).setProgresstotal((long)progresstotal);
//		moimList.getContent().get(i).setProgresspercent(progressPercent);
//		}
//
//	// 출력한다
//		model.addAttribute("categories",categories);
//		model.addAttribute("moimList", moimList);
//		long moimListcount = moimList.getTotalElements();// 각각 카운트를 센다
//		model.addAttribute("moimListcount", moimListcount);
//	} else {
//	Page<Moim> moimList = moimService.selectMoimAll(pageable);// 모든 모임리스트 출력한다
//	Moim moim=new Moim();
//
//	for(int i=0;i<moimList.getNumberOfElements();i++) {
//
//	List<ToDoWrite> todowrite =  moimList.getContent().get(i).getTodowrite();
//	double progresstotalSum=0;
//	double progresstotal=0;
//	double progressbefore = 0;
//	double progress = 0;
//	long count = 0;
//
//	for(ToDoWrite j: todowrite) {
//		Map<String, Object> tempMap = new HashMap<String, Object>();
//		tempMap.put("progress_done", j.getProgress_done());
//		tempMap.put("sametodoid",j.getMoim().getId());
//		progresstotalSum+=j.getProgress_done();
//		progresstotal+=j.getProgress_total();
//		progressbefore += j.getProgress();
//		count++;
//	}
//	if(count==0) {
//	count = 1;
//	}
//	
//		double progressPercent=0;
//		//progress = Double.parseDouble(String.format("%.2f",progressbefore / count));
//		progressPercent = Math.round(((progresstotalSum/progresstotal)*100)*100)/100.0;
//		moimList.getContent().get(i).setProgresssum((long)progresstotalSum);
//		moimList.getContent().get(i).setProgresstotal((long)progresstotal);
//		moimList.getContent().get(i).setProgresspercent(progressPercent);
//		moimList.getContent().get(i).setTodocount(count);
//	}
//	model.addAttribute("categories",categories);
//	model.addAttribute("moimList", moimList);
//	long moimListcount = moimList.getTotalElements();
//	model.addAttribute("moimListcount", moimListcount);
//	}
	return "moim/moimList";
	}



	@ResponseBody
	@GetMapping(value="/getMoimImage/{imageName:.+}",produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getMoimImage(@PathVariable("imageName") String imageName) throws Exception {
		return moimService.getMoimImage(imageName);
	}

	/**
	 * 모임 수정 화면으로
	 *
	 * @param
	 * @return
	 * @throws Exception
	 * @author choiseongjun
	 */
	@GetMapping("/moimUpdateView/{no}")
	public String moimUpdateView(@PathVariable("no") long no, Model model) {

		moimService.findMoimone(no).ifPresent(moimDetail -> model.addAttribute("moimDetail", moimDetail));

		model.addAttribute("no", no);

		return "moim/moimUpdate";
	}

	/**
	 * 모임 수정하기
	 *
	 * @param locale
	 * @param Moim
	 * @return
	 * @throws @author yuna
	 */
	@ResponseBody
	@PutMapping("/moimUpdate")
	public Map<String, Object> moimUpdate(@RequestPart("moim") Moim moim,
			@RequestPart(name = "file", required = false) MultipartFile file, HttpSession session) {
		Map<String, Object> returnData = new HashMap<String, Object>();

		String id = (String) session.getAttribute("peopleEmail");
		// String subject = moim.getCategory().getCommName();
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
		}

		try {
			moimService.updateMoim(id, moim, file);
			returnData.put("code", "1");
			returnData.put("message", "수정되었습니다");

		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}

		return returnData;
	}

	/**
	 * 모임 만들기 화면으로
	 *
	 * @param
	 * @return
	 * @throws Exception
	 * @author choiseongjun
	 */
	@GetMapping("/moimMakeView")
	public String moimMakeView() {
		return "moim/moimMake";
	}


	/**
	 * 모임 비공개 전환
	 *
	 * @param moimNo
	 * @param session
	 * @return
	 * @author hyojin
	 */
	@ResponseBody
	@PutMapping("/moimUpdateClosed/{moimNo}")
	public Map<String, Object> moimUpdateClosed(@PathVariable("moimNo") int moimNo, HttpSession session) {
		Map<String, Object> returnData = new HashMap<String, Object>();

		// String id = (String) session.getAttribute("peopleEmail");
		// System.out.println("id = " + id);

		try {
			moimService.updateMoimClosed(moimNo);
			returnData.put("code", "1");
			returnData.put("message", "비공개 전환 성공");
		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "비공개 전환 실패");
		}

		return returnData;
	}
	
	/**
	 * Ajax 비동기로 카테고리 별 모임 리스트 가져오기
	 *
	 * @param commonCode(categoryId)
	 * @return Map<String,Object>
	 * @author hyojin
	 */
	@GetMapping(path="/moimlist")
	@ResponseBody
	public Map<String,Object> moimList(@PageableDefault Pageable pageable,
			@RequestParam(name="commonCode", required=false /*, defaultValue = "00"*/) String commonCode) {

		// 1. 카테고리 영역( 전체 00 추가? )
		List<Common> categories = (List<Common>) moimService.getMoimElement().get("element_CA1");

		// 2. 모임 리스트 영역 by CommonCode (+ pageable)
		// 00 -> 전체
		Page<Moim> moims = commonCode.equals("00") ? moimService.selectMoimAll(pageable) : moimService.selectMoimByCategory(pageable, commonCode) ;

		// 3. 카테고리별 모임 리스트 개수
		long moimsCount = moims.getTotalElements();

		Map<String,Object> returnMap = new HashMap<>();
		returnMap.put("categories", categories);
		returnMap.put("moimList", moims);
		returnMap.put("moimListCount", moimsCount);

		return returnMap;
	}
	
}
