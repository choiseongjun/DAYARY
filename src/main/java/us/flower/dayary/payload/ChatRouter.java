package us.flower.dayary.payload;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.WebSocketSession;

import us.flower.dayary.controller.people.SessionNames;
import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimChat;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.DTO.Message;
import us.flower.dayary.repository.chat.MoimChatRepository;
import us.flower.dayary.repository.moim.MoimPeopleRepository;
import us.flower.dayary.repository.moim.MoimRepository;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.service.moim.moimService;

@Controller
public class ChatRouter {

	@Autowired
	MoimRepository moimRepository;
	@Autowired
	moimService moimService;
	@Autowired
	MoimChatRepository moimchatRepository;
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	@Autowired
	MoimPeopleRepository moimpeopleRepository;
	@Autowired
	private PeopleRepository peopleRepository;

	
	private String getId(HttpSession session) {
		String userid = (String) session.getAttribute("PeopleId");
		return userid;
		
//		People loginUser = (People)httpSession.get(SessionNames.LOGIN);
//		if (null == loginUser) 
//			return session.getId();
//		else
//			return String.valueOf(loginUser.getId()); 	
			//Long.toString(loginUser.getId()); 	
	}

	@MessageMapping("/moimchat")
	@SendTo("/topic/message")
	public Message ttt(Message message) throws Exception{
		
		Date date=new java.sql.Date(System.currentTimeMillis());
		message.setCreateDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));
		People people=new People();
		people.setId(message.getPeopleId());
		
		Optional<People> user = peopleRepository.findById(message.getPeopleId());
		
		Moim moim=new Moim();
		moim.setId(message.getMoimNo());
		
		if(user.get().getImageName()==null) {
			message.setImageName("/images/default_people");
			message.setImageExtension("png");
		}else { 
			message.setImageName("/getMoimImage/"+user.get().getImageName());
			message.setImageExtension(user.get().getImageExtension());
		}
		
		MoimChat moimchat=new MoimChat();
		moimchat.setPeople(people);
		moimchat.setChatMemo(message.getMsg());
		moimchat.setMoim(moim);
		moimchat.setCreateDate(date);
		messagingTemplate.convertAndSend("/topic/" + message.getPeopleId(), message.getMsg());
		moimchatRepository.save(moimchat);
		return message;
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
    public String moimChatroom(@PathVariable("no") long no,Model model,HttpSession session) {
    	long peopleId = (long) session.getAttribute("peopleId");//일반회원 번호를 던져준다
    	String email = (String) session.getAttribute("peopleEmail");
    	Optional<Moim> moimOne=moimRepository.findById(no);
        //List<People> moimpeopleList=moimOne.get().getPeopleList();
        List<MoimPeople> moimpeopleList = moimpeopleRepository.findByMoim_id(no);// 이건 모임내 전체사람조회
        moimService.findMoimone(no).ifPresent(moimDetail -> model.addAttribute("moimDetail", moimDetail));//모임장중심으로 데이터 불러옴
        //가입한 이후의 리스트만 들고온다
        MoimPeople myMoimPeopleInfo = moimpeopleRepository.findByMoim_idAndPeople_id(no, peopleId).get(0);
        System.out.println(myMoimPeopleInfo.getCreatedAt()+"//////////");
        System.out.println(myMoimPeopleInfo.getCreatedAt()+"//////////");
        List<MoimChat> moimchatList=moimchatRepository.findByMoim_idAndCreateDateBetween(no,Timestamp.valueOf(myMoimPeopleInfo.getCreatedAt()),Timestamp.valueOf(LocalDateTime.now()));//특정모임의 채팅리스트를 들고온다( ex)모임1번의 채팅리스트)
        //채팅방확인시간업데이트
        myMoimPeopleInfo.setUpdatedAt(LocalDateTime.now());
        moimpeopleRepository.save(myMoimPeopleInfo);
        
        model.addAttribute("moimchatList",moimchatList);
        model.addAttribute("moimpeopleList",moimpeopleList);
        model.addAttribute("moimOne",moimOne); 
        model.addAttribute("no",no);
        model.addAttribute("peopleId",peopleId);
        model.addAttribute("email",email);
    	return "moim/moimChatroom";
    }
    /**
     * 모임 단체채팅방 조회
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun 
     */
    @GetMapping("/moimchat/up/{no}")
    @ResponseBody
    public Map<String,Object> moimChatUp(@PathVariable("no") long no,Model model,HttpSession session) {
    	long peopleId = (long) session.getAttribute("peopleId");//일반회원 번호를 던져준다
   
    	MoimPeople myMoimPeopleInfo = moimpeopleRepository.findByMoim_idAndPeople_id(no, peopleId).get(0);
    	//채팅방확인시간업데이트
    	myMoimPeopleInfo.setUpdatedAt(LocalDateTime.now());
    	
    	moimpeopleRepository.save(myMoimPeopleInfo);
    	Map<String,Object> data=new HashMap<String,Object>();
		data.put("code","1");
		
		
		return data;
    }
}
