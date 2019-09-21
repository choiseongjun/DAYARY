package us.flower.dayary.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.flower.dayary.domain.BoardGroup;
import us.flower.dayary.domain.CommunityBoard;
import us.flower.dayary.domain.People;
import us.flower.dayary.repository.CommunityBoardRepository;
import us.flower.dayary.service.CommunityBoardService;

@Controller
public class CommunityController {
	
	@Autowired
	CommunityBoardService communityBoardService;
	
	@Autowired
	CommunityBoardRepository communityBoardRepository;
	/**
     * 커뮤니티리스트(Timeline) 글삭제
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
	@ResponseBody
	@DeleteMapping("/community/communityList/{board_group_no}/communityDelete/{timeLineListNo}")
	public Map<String, Object> CommunityTimeLinedelete(@PathVariable("timeLineListNo") long timeLineListNo,Model model,Sort sort) {
	
		 Map<String, Object> returnData = new HashMap<String, Object>();
		
		 
		
		try {
			communityBoardService.deleteBoardone(timeLineListNo);
		  	returnData.put("code", "1");
            returnData.put("message", "삭제되었습니다");

        } catch (Exception e) {
            returnData.put("code", "E3290");
            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
        }
 

		return returnData;
	}
	 /**
     * 커뮤니티 타임라인 글쓰기
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
	@ResponseBody
	@PostMapping("/community/communityList/{board_group_no}/communityWrite")
	public Map<String, Object> CommunityListWrite(@PathVariable("board_group_no") long board_group_no
									,@RequestBody CommunityBoard communityBoard,HttpSession session) {
		
		 Map<String, Object> returnData = new HashMap<String, Object>();
		 
		 if (communityBoard.getMemo().equals(null) || communityBoard.getMemo().equals("")) {
	            returnData.put("code", "0");
	            returnData.put("message", "내용을 입력해주세요");
	            return returnData;
	        }
		 
		SimpleDateFormat format = new SimpleDateFormat ( "yyyy년 MM월dd일 HH시mm분ss초");
		String format_time = format.format (System.currentTimeMillis());
		
		Long people_no = (Long) session.getAttribute("peopleId");//사용자세션정보 들고오기
		 
		try {
			  	communityBoardService.communityWrite(people_no,board_group_no,communityBoard);
			  	returnData.put("code", "1");
	            returnData.put("message", "저장되었습니다");

	        } catch (Exception e) {
	            returnData.put("code", "E3290");
	            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
	        }
		
		
		 
		
		
		return returnData;
	}
	 /**
     * 커뮤니티리스트(Timeline) 조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
	@GetMapping("/community/communityList/{board_group_no}")
	public String CommunityView(@PathVariable("board_group_no") long board_group_no,Model model,Sort sort) {
	
		
		//sort=sort.and(new Sort(Sort.Direction.DESC));
		 
		model.addAttribute("board_group_no",board_group_no);



		List<CommunityBoard> timeLineList=communityBoardService.CommunityList();
		model.addAttribute("timeLineList",timeLineList);
		return "community/communityList";
	}
	/**
     * 커뮤니티리스트 조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
	@GetMapping("/community/communityList/studyList/{board_group_no}")
	public String studyList(@PathVariable("board_group_no") long board_group_no,Model model) {
		
		model.addAttribute("board_group_no",board_group_no);
		
		return "community/comunitystudyList";
	}
	/**
     * 커뮤니티리스트 조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
	@GetMapping("/community/communityList/studyWrite/{board_group_no}")
	public String studyWrite(@PathVariable("board_group_no") long board_group_no) {
		
		return "community/comunitystudyWrite";
	}
	 /**
     * 커뮤니티 StudyCafeList 조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
	@GetMapping("/community/studycafeList/{board_group_no}")
	public String studcafeList(@PathVariable("board_group_no") long board_group_no) {
		
		return "community/studycafeList";
	}
	/**
	 * 커뮤니티 StudyCafeList 작성
	 *
	 * @param
	 * @return
	 * @throws
	 * @author choiseongjun
	 */
	@GetMapping("/community/communityList/studycafeWrite/{board_group_no}")
	public String studyCafeWrite(@PathVariable("board_group_no") long board_group_no) {

		return "community/studycafeWrite";
	}
	 /**
     * 커뮤니티 StudyCafeDetail 상세보기
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun
     */
	@GetMapping("/community/studycafeDetail")
	public String studycafeDetail() {
		
		return "community/studycafeDetail";
	}
}
