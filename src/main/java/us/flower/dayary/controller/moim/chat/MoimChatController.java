package us.flower.dayary.controller.moim.chat;

import java.text.SimpleDateFormat;
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
import org.springframework.web.socket.WebSocketSession;

import us.flower.dayary.controller.people.SessionNames;
import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimChat;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.Noti;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.DTO.Message;
import us.flower.dayary.domain.DTO.MoimJoinDTO;
import us.flower.dayary.repository.NotifyRepository;
import us.flower.dayary.repository.chat.MoimChatRepository;
import us.flower.dayary.repository.moim.MoimPeopleRepository;
import us.flower.dayary.repository.moim.MoimRepository;
import us.flower.dayary.service.moim.moimService;

@Controller
public class MoimChatController {

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
	NotifyRepository notifyRepository;
	
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
	
	/**
     * 모임 단체채팅방 채팅 날리기
     *
     * @param 
     * @return
     * @throws 
     * @author choiseongjun 
     * @Date 2019-10-02
     */
	@MessageMapping("/moimjoinNoti")
	@SendTo("/topic/message")
	public MoimJoinDTO moimjoinNoti(MoimJoinDTO message) throws Exception{
		HttpSession session;
		 
	String moimPeopleList = message.getMoimPeopleListstr();
	String[] moimPeopleListstr = moimPeopleList.split(",");

	
	
	for (int i = 0; i < moimPeopleListstr.length; i++) {
	
		String moimNo1 = message.getMoimNo();
		String moimtitle = message.getMoimTitle();
		String PeopleEmail = message.getPeopleEmail();
		
		System.out.println(moimNo1+moimtitle+PeopleEmail);
		System.out.println(moimPeopleListstr[i]);
		
		int moimNo = Integer.parseInt(moimNo1);//모임번호
		int notipeople = Integer.parseInt(moimPeopleListstr[i]);//알림받을유저
		
		Moim moim = new Moim();
		moim.setId(moimNo);
		
		People people=new People();
		people.setId(notipeople);
		
		Noti noti=new Noti();//알림객체를 들고온다
		noti.setPeople(people);
		noti.setMemo(moimtitle+"에   "+PeopleEmail+"님이  가입하셨습니다!");
		noti.setMoim(moim);
		noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
		notifyRepository.save(noti);
		
		
	}
	

		return message;
		
	}
	@MessageMapping("/moimchat")
	@SendTo("/topic/message")
	public Message ttt(Message message) throws Exception{
		
		System.out.println("ID?="+message.getPeopleId());
		System.out.println("MSG=" + message.getMsg());
		System.out.println("MoimNo=" + message.getMoimNo());
		System.out.println("peopleEmail"+message.getPeopleEmail());
		Date date=new java.sql.Date(System.currentTimeMillis());
		message.setCreateDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));
		People people=new People();
		people.setId(message.getPeopleId());
		
		Moim moim=new Moim();
		moim.setId(message.getMoimNo());
		
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
        List<MoimChat> moimchatList=moimchatRepository.findByMoim_id(no);//특정모임의 채팅리스트를 들고온다( ex)모임1번의 채팅리스트)
       
        model.addAttribute("moimchatList",moimchatList);
        model.addAttribute("moimpeopleList",moimpeopleList);
        model.addAttribute("moimOne",moimOne); 
        model.addAttribute("no",no);
        model.addAttribute("peopleId",peopleId);
        model.addAttribute("email",email);
    	return "moim/moimChatroom";
    }
}
