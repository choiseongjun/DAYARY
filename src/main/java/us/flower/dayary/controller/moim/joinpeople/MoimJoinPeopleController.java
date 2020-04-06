package us.flower.dayary.controller.moim.joinpeople;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import net.minidev.json.JSONObject;
import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.DTO.TempData;
import us.flower.dayary.repository.moim.MoimPeopleRepository;
import us.flower.dayary.service.moim.moimService;
import us.flower.dayary.service.moim.joinpeople.MoimJoinPeopleService;

@Controller
public class MoimJoinPeopleController {

    @Autowired
    private moimService moimService;
    @Autowired
    MoimJoinPeopleService moimjoinPeopleService;
    @Autowired
    MoimPeopleRepository moimpeopleRepository;
    /**
	 * 일반회원 모임가입 승인(모임장)
	 *
	 * @param locale
	 * @param Moim
	 * @return returnData
	 * @throws Exception
	 * @author choiseongjun
	 * @story TempDataDTO로 임시데이터를 받음 
	 * @date 2019-10-07
	 */
   
	@ResponseBody
	@PostMapping("/moimParticipant/moimgrantjoinPeople")
	public Map<String, Object> moimgrantjoinPeople(@Valid @RequestBody TempData tempdata,HttpSession session) {
	
		
		System.out.println(tempdata.getData1());
		
			
		  Map<String,Object> returnData = new HashMap<String,Object>();
		  People people=new People();
			people.setId(tempdata.getNo1());
			
			
			Moim moim=new Moim();
			moim.setId(tempdata.getNo2());
		  
			long peopleId=tempdata.getNo1();
			long moimId=tempdata.getNo2();
		   
			
			 
		 try {
			 		moimpeopleRepository.updateMoimPeoplejoinCondition(people,moim);

				 	returnData.put("code","1"); 
				 	returnData.put("message","가입 승인:)");
			 
			
		  }catch(Exception e) { returnData.put("code", "E3290");
		  		returnData.put("message", "데이터 확인 후 다시 시도해주세요."); 
		  }
		 
		return returnData;
	}
  
	/**
	 * 일반회원 모임 강퇴(모임장)
	 *
	 * @param locale
	 * @param Moim
	 * @return returnData
	 * @throws Exception
	 * @author choiseongjun
	 * @story TempDataDTO로 임시데이터를 받아서 그걸 다시 Moim과 People에 담아서 제거한 로직   
	 */
   
	@ResponseBody
	@PostMapping("/moimParticipant/banjoinedPeople")
	public Map<String, Object> moimbanjoinedPeople(@Valid @RequestBody TempData tempdata,HttpSession session) {
	
		
			People people=new People();
			people.setId(tempdata.getNo1());
			
			Moim moim=new Moim();
			moim.setId(tempdata.getNo2());
			
			
			
		  Map<String,Object> returnData = new HashMap<String,Object>();
		  
		   
		 try { 
			 	moimjoinPeopleService.banMoimpeople(people,moim);
			 	returnData.put("code","1"); 
			 	returnData.put("message","회원강퇴 완료:)");
		  }catch(Exception e) { returnData.put("code", "E3290");
		  		returnData.put("message", "데이터 확인 후 다시 시도해주세요."); 
		  }
		 
		return returnData;
	}
	/**
	 * 일반회원 모임 참가(가입승인조건 받아야하는경우) 
	 *
	 * @param locale
	 * @param Moim
	 * @return returnData
	 * @throws Exception
	 * @author choiseongjun
	 */
	@ResponseBody
	@PostMapping("/moimParticipantY/{moimNo}")
	public Map<String, Object> moimParticipantY(@PathVariable("moimNo") long moimNo,HttpSession session) {
		Long peopleId = (Long) session.getAttribute("peopleId");
	
		
		char joinCondition='N';//참가자 승인전 N
		Map<String,Object> returnData = new HashMap<String,Object>();
	
		try {	
				char maker='N';//만든사람여부
				moimService.moimParticipant(peopleId,moimNo,joinCondition,maker);
				returnData.put("code","1");
				returnData.put("message","모임장의 승인을 받아야합니다:)");
		}catch(Exception e) {
			    returnData.put("code", "E3290");
	            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}
		return returnData;
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
		
		char joinCondition='Y';//참가자 승인후 Y
		Map<String,Object> returnData = new HashMap<String,Object>();

		Map<String,String> CountMoimPeople = moimjoinPeopleService.selectCountMoimPeople(moimNo);
		
		
		
		try {	
			if(CountMoimPeople.get("cntPeople").equals("TRUE")) {
				returnData.put("code","2");  
				returnData.put("message","인원 초과했어요:)");
			}else {
				char maker='N';//만든사람여부
				moimService.moimParticipant(peopleId,moimNo,joinCondition,maker);
				returnData.put("code","1");  
				returnData.put("message","모임가입완료:)");
			}
		}catch(Exception e) {
			    returnData.put("code", "E3290");
	            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}
		return returnData;
	}
	/**
	 * 일반회원 모임 탈퇴 
	 *
	 * @param locale
	 * @param moimPeopleNo
	 * @return returnData
	 * @throws Exception
	 * @author choiseongjun
	 * @Date 2019-09-24
	 */
	@ResponseBody
	@DeleteMapping("/moimParticipant/deletejoinedPeople/{moimPeopleNo}")
	public Map<String, Object> moimdeletejoinedPeople(@PathVariable("moimPeopleNo") long moimPeopleNo,HttpSession session) {
		Long peopleId = (Long) session.getAttribute("peopleId");
	
		
		Map<String,Object> returnData = new HashMap<String,Object>();
	
		  try {
			  	moimjoinPeopleService.deleteMoimPeople(moimPeopleNo);
				returnData.put("code","1");
				returnData.put("message","모임탈퇴 완료:)");
		  }catch (Exception e) {
	            returnData.put("code", "E3290");
	            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
	      }
		return returnData;
	}

	/**
	 * 모임 회원 조회
	 *
	 * @param locale
	 * @param moimNo
	 * @return returnData
	 * @throws Exception
	 * @author yn
	 * @Date 2019-11-17
	 */
	@ResponseBody
	@GetMapping("/moimParticipant/searchJoinedPeople/{moimNo}")
	public JSONObject searchJoinedPeople(@PathVariable("moimNo") long moimNo, HttpSession session) {
		
		JSONObject returnData = new JSONObject();
		
		try {
			returnData.put("moimPeople", moimjoinPeopleService.searchJoinedPeople(moimNo));
			returnData.put("code","1");
			returnData.put("message","모임가입자 조회완료");
		}catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}
		return returnData;
	}
}
