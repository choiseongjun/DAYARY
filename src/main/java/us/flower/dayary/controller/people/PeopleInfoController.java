package us.flower.dayary.controller.people;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.jdo.annotations.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import us.flower.dayary.common.FileManager;
import us.flower.dayary.common.TokenGenerator;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.Noti;
import us.flower.dayary.domain.People;
import us.flower.dayary.repository.moim.MoimPeopleRepository;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.service.noti.NotiService;
import us.flower.dayary.service.people.PeopleInfoService;

@Controller
public class PeopleInfoController {

	@Autowired
	PeopleInfoService peopleinfoService;
	@Autowired
	PeopleRepository peopleRepository;
	@Autowired
	MoimPeopleRepository moimpeopleRepository;
	@Autowired
	NotiService notiService;
	@Autowired
	private TokenGenerator tokenGenerator;
	@Autowired
	private FileManager fileManager;
	@Value("${moimImagePath}")
	private String moimImagePath;
  /**
   * 사진 불러오기 by choiseongjun
   *
   * @param 
   * @return
   * @throws 
   * @author choiseongjun
   * @date 2019-10-01
   */
	@ResponseBody
    @GetMapping("/getPeopleImage/{imageName:.+}")
    public byte[] getMoimImage(@PathVariable("imageName") String imageName) throws Exception {
        return peopleinfoService.getMoimImage(imageName);
    }
	
	  /**
     * 모임 삭제  by choiseongjun 
     *
     * @param peopleId
     * @return
     * @throws 
     * @author choiseongjun
     * @date 2019-10-01
     */
	@Transactional
	@ResponseBody
	@DeleteMapping("/myprofileView/deletePeople/{peopleId}")
	public Map<String, Object> MoimDeleteOne(@PathVariable("peopleId") long peopleId,Model model,HttpServletRequest request) {
	
		Map<String, Object> returnData = new HashMap<String, Object>();

		
		try {
			
			peopleinfoService.deletePeople(peopleId);//회원탈퇴
			
		  	returnData.put("code", "1");
            returnData.put("message", "삭제되었습니다");

        } catch (Exception e) {
            returnData.put("code", "E3290");
            returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
        }
 

		return returnData;
	}
	/**
	 * 내 알림리스트 카운트조회
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@ResponseBody
	@GetMapping("/getMyNotiListCount")
	public Map<String,Object> getMyNotiCount(HttpSession session,Model model) {
		long peopleId = (long) session.getAttribute("peopleId");//일반회원 번호를 던져준다.
		
	

		long getMyNotiCount=notiService.totalNoti(peopleId);
		
		
		Map<String,Object> data=new HashMap<String,Object>();
		data.put("getMyNotiCount",getMyNotiCount);
		data.put("code", "1"); 	
		
		return data;
	}
	/**
	 * 알림리스트 조회
	 *
	 * @param 조회할 페이지리스트
	 * @return
	 * @throws @author JY
	 */
	@ResponseBody
	@GetMapping("/getMyNotiList/{pageNum}")
	public Map<String,Object> getMyNotiList(HttpSession session,@PathVariable("pageNum")int pageNum) {
		//회원 번호를 던져준다.
		long peopleId = (long) session.getAttribute("peopleId");

		List<Noti> notiList=notiService.getMoreNoti(pageNum, peopleId, 'P');
		
		Map<String,Object> data=new HashMap<String,Object>();
		data.put("notiList",notiList);
		
		return data;
	}
	/**
	 * 내 정보 조회
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@GetMapping("/myprofileView")
	public String myprofileView(HttpSession session,Model model) {
		long peopleId = (long) session.getAttribute("peopleId");//일반회원 번호를 던져준다.
		
		peopleRepository.findById(peopleId).ifPresent(peopleinfo->model.addAttribute("peopleinfo",peopleinfo));
		
		List<MoimPeople> joinedMoim=moimpeopleRepository.findByPeople_id(peopleId);
		
		model.addAttribute("joinedMoim",joinedMoim);
		return "people/myprofile";
	}
	/**
	 * 내 정보 수정
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@ResponseBody
	@PutMapping("/myprofileViewUpdate/{peopleId}")
	public ResponseEntity<?> myprofileViewUpdate(@PathVariable("peopleId") long peopleId,HttpSession session, @RequestPart(name="file",required=false) MultipartFile file) {
		
		System.out.println("파일은??");
		System.out.println(file);
		Optional<People> user = peopleRepository.findById(peopleId);
		if(file!=null) {	//파일이 null이 아니면 
			//이미지파일이름생성
	        String imageName="";
			while(true){
	        	imageName=tokenGenerator.getToken();
				//DB에 파일이름이 존재하지 않으면 moim domain에 set
	        	if(!peopleRepository.existsByImageName(imageName)){
	        		
					user.get().setImageName(imageName);
					break; 
				} 
			}
	  
			
	        //이미지파일확장자추출
	        String originalFileName = file.getOriginalFilename();
	        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
	        user.get().setImageExtension(fileExtension);

	        //파일업로드
	        try { 
	            fileManager.fileUpload(file, moimImagePath+"/"+imageName+"."+fileExtension);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        People result = peopleRepository.save(user.get());
		}	
		
		
		return null;
	}
	/**
	 * 내가 가입한 모임리스트 조회
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@ResponseBody
	@GetMapping("/myprofilemoimView")
	public Map<String,Object>  myprofilemoimView(HttpSession session,Model model) {
		long peopleId = (long) session.getAttribute("peopleId");//일반회원 번호를 던져준다.
		People people =new People();
		people.setId(peopleId);
		Map<String,Object> data=new HashMap<String,Object>();
		
		List<String> joinedMoimListNo=moimpeopleRepository.findMoimNo(people);
		List<String> joinedMoimList=moimpeopleRepository.findMoimName(people);
		
		data.put("joinedMoimListNo",joinedMoimListNo);
		data.put("joinedMoimList",joinedMoimList);
		data.put("code", "1"); 	
	
		return data;
	}
	/**
	 * 내가 만든 모임리스트 조회
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@ResponseBody
	@GetMapping("/myprofilemademoimView")
	public Map<String,Object>  myprofilemademoimView(HttpSession session,Model model) {
		long peopleId = (long) session.getAttribute("peopleId");//일반회원 번호를 던져준다.
		People people =new People();
		people.setId(peopleId);
		Map<String,Object> data=new HashMap<String,Object>();
		
		List<String> madeMoimListNo=moimpeopleRepository.findmadeMoimNo(people);
		List<String> madeMoimListtitle=moimpeopleRepository.findmadeMoimName(people);
		
		data.put("madeMoimListNo",madeMoimListNo);
		data.put("madeMoimListtitle",madeMoimListtitle);
		data.put("code", "1"); 	
	
		return data;
	}
	/**
	 * 모임에서 회원정보 조회
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@GetMapping("/moimpeopleInfo/{peopleId}")
	public String moimpeopleInfo(@PathVariable("peopleId") long peopleId,Model model) {
		List<MoimPeople> joinedMoim=moimpeopleRepository.findByPeople_id(peopleId);
		Optional<People> peopleinfo=peopleRepository.findById(peopleId);
		model.addAttribute("peopleinfo",peopleinfo.get());
		model.addAttribute("joinedMoim",joinedMoim);
		
		return "people/peopleinfo";
	}
	/**
	 * 모임디테일에서 회원 누르면 모임가입한거 리스트조회
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@ResponseBody
	@GetMapping("/peopleInfomoimList/{peopleId}")
	public Map<String,Object>  peopleInfomoimList(@PathVariable("peopleId") long peopleId,Model model) {
		Map<String,Object> data=new HashMap<String,Object>();
		People people =new People();
		people.setId(peopleId);
		List<String> joinedMoimListNo=moimpeopleRepository.findMoimNo(people);
		List<String> joinedMoimList=moimpeopleRepository.findMoimName(people);
		
		data.put("joinedMoimListNo",joinedMoimListNo);
		data.put("joinedMoimList",joinedMoimList);
		data.put("code", "1"); 	
	
		return data;
	}
	/**
	 * 모임디테일에서 회원 누르면 모임 만든거 리스트조회
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@ResponseBody
	@GetMapping("/peopleInfomademoimList/{peopleId}")
	public Map<String,Object>  peopleInfomademoimList(@PathVariable("peopleId") long peopleId,Model model) {
		Map<String,Object> data=new HashMap<String,Object>();
		People people =new People();
		people.setId(peopleId);
		List<String> madeMoimListNo=moimpeopleRepository.findmadeMoimNo(people);
		List<String> madeMoimListtitle=moimpeopleRepository.findmadeMoimName(people);
		
		data.put("madeMoimListNo",madeMoimListNo);
		data.put("madeMoimListtitle",madeMoimListtitle);
		data.put("code", "1"); 	
	
		return data;
	}
}
