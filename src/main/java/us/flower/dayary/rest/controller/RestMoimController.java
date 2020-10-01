package us.flower.dayary.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.minidev.json.JSONObject;
import us.flower.dayary.domain.Meetup;
import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.Tag;
import us.flower.dayary.domain.ToDoWrite;
import us.flower.dayary.repository.moim.MoimPeopleRepository;
import us.flower.dayary.repository.moim.MoimRepository;
import us.flower.dayary.repository.moim.meetup.MoimMeetUpRepository;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.security.JwtTokenProvider;
import us.flower.dayary.service.moim.moimService;

@RestController
public class RestMoimController {

	@Autowired
	moimService moimService;
	@Autowired
	MoimRepository moimrepository;
	@Autowired
	MoimMeetUpRepository moimmeetupRepository;
	@Autowired
	MoimPeopleRepository moimpeopleRepository;
	@Autowired
	PeopleRepository peopleRepository;
	@Autowired
	JwtTokenProvider tokenProvider;
	
	/**

	 * 모임 리스트 출력(Paging 처리)
	 *
	 * @param locale
	 * @param Moim
	 * @return moimList
	 * @throws Exception
	 * @author choiseongjun
	 */
	@GetMapping("/rest/moimlistView/{commCode}")
	public ResponseEntity<?> moimListView(@PageableDefault Pageable pageable, HttpSession session,@PathVariable("commCode") String commCode
			) {

		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
		pageable = PageRequest.of(page, 9, Sort.Direction.DESC, "id");// 내림차순으로 정렬한다
		JSONObject returnData = new JSONObject();
		if(commCode.equals("01")) {
			Page<Moim> moimList = moimService.selectMoimAll(pageable);// 모든 모임리스트 출력한다

			for(int i=0;i<moimList.getNumberOfElements();i++) {
				
				
				List<ToDoWrite> todowrite =  moimList.getContent().get(i).getTodowrite();
				double progresstotalSum=0;
				double progresstotal=0;
				double progressbefore = 0;
				double progress = 0;
				long count = 0; 
				
				String hashtag="";
				for(int j=0;j<moimList.getContent().get(i).getMoimtag().size();j++) {
				Tag tags =moimList.getContent().get(i).getMoimtag().get(j).getTag();
					hashtag +="#"+tags.getName();
				}
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
					moimList.getContent().get(i).setHashtag(hashtag);
				}
			
			returnData.put("moimList", moimList);
			long moimListcount = moimList.getTotalElements();
			returnData.put("moimListcount", moimListcount);
		}else {
			Page<Moim> moimList = moimService.selectMoimCate(pageable,commCode);// 카테고리별로 모임리스트 출력한다
			

			
			for(int i=0;i<moimList.getNumberOfElements();i++) {
				
				List<ToDoWrite> todowrite =  moimList.getContent().get(i).getTodowrite();
				double progresstotalSum=0;
				double progresstotal=0;
				double progressbefore = 0;
				double progress = 0;
				long count = 0;
				
				//Set<Tag> tags = moimList.getContent().get(i).getTags();
				String hashtag="";
				for(int j=0;j<moimList.getContent().get(i).getMoimtag().size();j++) {
					Tag tags =moimList.getContent().get(i).getMoimtag().get(j).getTag();
						hashtag +="#"+tags.getName();
				}
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
					moimList.getContent().get(i).setHashtag(hashtag);
				}
			
			
			
			returnData.put("moimList", moimList);
			long moimListcount = moimList.getTotalElements();
			returnData.put("moimListcount", moimListcount);
			returnData.put("test", "on");
			
			
		}
		
		 return new ResponseEntity<>(returnData, HttpStatus.OK);

		
	}
	@GetMapping("/rest/moimlistView/moimdetailView/{no}")
	public ResponseEntity<?> RestmoimDetailView(@PathVariable("no") long no,@RequestHeader (name="Authorization", required=false) String token, Model model, HttpSession session, Sort sort,
			@PageableDefault Pageable pageable) {
		JSONObject returnData = new JSONObject();
		System.out.println(token);
		if(tokenProvider.validateToken(token)) {
			System.out.println(tokenProvider.getUserIdFromJWT(token));
			Long userId=tokenProvider.getUserIdFromJWT(token);
			System.out.println(userId);
			String moimPeopleNo = moimService.findMoimPeopleNoOne(userId, no);// 참여자단건 조회(모임피플넘버를 단건으로 가져와서 moimPeople_no에
			// 넣어준다)
			List<MoimPeople> joinedpeoplelist = moimpeopleRepository.findByMoim_idAndPeople_id(no, userId);// 현재 접속한 유저
			// 리스트를 들고옴
			returnData.put("PeopleNo",userId);
			returnData.put("moimPeopleNo",moimPeopleNo);
			returnData.put("joinedpeoplelist",joinedpeoplelist);
		}
		
		moimService.findMoimone(no).ifPresent(moimDetail -> returnData.put("moimDetail", moimDetail));// 모임장중심으로 데이터
		
		List<Meetup> meetupList = moimmeetupRepository.findByMoim_id(no, pageable);//오프라인모임리스트
		returnData.put("meetupList",meetupList);
		// 불러옴
		 return new ResponseEntity<>(returnData, HttpStatus.OK);
	}
	@PostMapping("/rest/moimMake")
	public Map<String, Object> RestmoimMake(@RequestPart("moim") Moim moim, 
			@RequestPart(name = "file", required = false) MultipartFile file,@RequestHeader (name="Authorization", required=false) String token) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		
		System.out.println(token);
		System.out.println(moim.toString());
		System.out.println(file.getOriginalFilename());
		if(tokenProvider.validateToken(token)) {
			Long PeopleNo=tokenProvider.getUserIdFromJWT(token);
			Optional<People> people = peopleRepository.findById(PeopleNo);
			String email = people.get().getEmail();
			String subject = "영어";
			
			moim.setRecruitStatus("모집중");
			
			//moimService.saveMoim(email, subject, moim, file);
		}
		System.out.println(moim.getTitle());
		System.out.println(moim.getIntro());
		System.out.println("Controlloer");
		System.out.println(file.getOriginalFilename());
		System.out.println(file.getSize());
		
		return returnData;

		//String id = (String) session.getAttribute("peopleEmail");
		//Long peopleId = (Long) session.getAttribute("peopleId");
//		String subject = moim.getCategory().getCommName();
//		if (id.equals(null) || id.equals("")) {
//			returnData.put("code", "0");
//			returnData.put("message", "로그인 후 이용해주세요");
//			return returnData;
//		} else if (moim.getTitle().equals(null) || moim.getTitle().equals("")) {
//			returnData.put("code", "0");
//			returnData.put("message", "모임 이름을 입력해주세요");
//			return returnData;
//		} else if (moim.getPeopleLimit() == 0) {
//			returnData.put("code", "0");
//			returnData.put("message", "인원수를 입력해주세요");
//			return returnData;
//		} else if (moim.getCategory() == null || moim.getCategory().equals("")) {
//			returnData.put("code", "0");
//			returnData.put("message", "모임 주제를 선택해주세요");
//			return returnData;
//		} else if (moim.getSidocode() == null || moim.getSidocode().equals("") || moim.getSigooncode() == null
//				|| moim.getSigooncode().equals("")) {
//			returnData.put("code", "0");
//			returnData.put("message", "활동 지역을 선택해주세요");
//			return returnData;
//		}
//		// [hyozkim] 추가
//		else if(moim.getSecretCondition() == null || moim.getSecretCondition().equals("") ) {
//			returnData.put("code", "0");
//			returnData.put("message", "비공개 설정을 선택해주세요");
//			return returnData;
//		} else if(moim.getRecruitStatus() == null || moim.getRecruitStatus().equals("") ) {
//			returnData.put("code", "0");
//			returnData.put("message", "모임 상태를 선택해주세요");
//			return returnData;
//		}
//		char joinCondition='Y';//참가자 승인후 Y Defualt Value
//		try {
//			moimService.saveMoim(id, subject, moim, file);
//			
//			long MoimId = moimService.selectMaxMoimId();
//			
//			//모임참가자 서비스를 들고와서 재사용한다(모임참가자테이블에도넣기위함 알림을 모임참가자테이블로 보내기에 모임장도 넣어야한다 ) by choiseongjun 20191221
//			moimService.moimParticipant(peopleId,MoimId,joinCondition);
//			returnData.put("code", "1");
//			returnData.put("message", "저장되었습니다");
//
//		} catch (Exception e) {
//			returnData.put("code", "E3290");
//			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
//		}
//		return returnData;
	}
}