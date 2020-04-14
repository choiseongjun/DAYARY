package us.flower.dayary.payload;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.Noti;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.DTO.MoimJoinDTO;
import us.flower.dayary.repository.moim.MoimRepository;
import us.flower.dayary.repository.noti.NotiRepository;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.service.noti.NotiService;

@Controller
public class NotiRouter {


	@Autowired
	private NotiService notiService;
	@Autowired
	PeopleRepository peopleRepository;
	@Autowired
	MoimRepository moimRepository;

	@MessageMapping("/noti/joinNoti")
	@SendTo("/topic/noti")
	public void noti(MoimJoinDTO message) throws Exception {
			String moimOne=message.getMoimTitle() + "에   " + message.getUserName() + "님이  가입하셨습니다:)";
			String newOne=message.getMoimTitle()+"에 가입하셨습니다:)";
			notiService.sendNotiToMoimOne(message,moimOne,newOne );

	
	}


	/**
	 * 모임 가입시 팀안 알림 날리기
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 * @Date
	 */
	@MessageMapping("/moim/joinNoti")
	@SendTo("/topic/moimNoti")
	public void moimNoti(MoimJoinDTO message) throws Exception {
		// 모입가입시 팀내알림

		notiService.sendNotiToMoim(message, message.getUserName() + "님이  가입하셨습니다:)");


	}

	/**
	 * 모임 가입 알림 날리기
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 * @Date
	 */
	@MessageMapping("/noti/apprNoti")
	@SendTo("/topic/noti")
	public void moimJoinNoti(MoimJoinDTO message) throws Exception {
		// 승인필요한 모임가입시 모임장에게알림
		String msg=message.getMoimTitle() + "에 " + message.getUserName() + "님의 가입 요청을 승인해주세요:)" ;
		Moim moim=moimRepository.findById(Long.parseLong(message.getMoimNo())).get();
		notiService.sendNotiToPrivate(message,msg,moim.getPeople().getId() );

	}

	/**
	 * 모임 탈퇴알림 날리기
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 * @Date
	 */
	@MessageMapping("/moim/exitNoti")
	@SendTo("/topic/moim")
	public void ExitNoti(MoimJoinDTO message) throws Exception {

		notiService.sendNotiToMoim(message, message.getUserName() + "님이 탈퇴하셨습니다:(");
	}

	/**
	 * 모임 탈퇴알림 날리기(개인)
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 * @Date
	 */
	@MessageMapping("/noti/exitNoti")
	@SendTo("/topic/noti")
	public void moimExitNoti(MoimJoinDTO message) throws Exception {


		//username을 peopleId로 받아서 탈퇴한 사람 찾기
		People p = peopleRepository.findById(Long.parseLong(message.getUserName())).get();
				
		message.setUserName(p.getName());
		
		String moimOne=message.getMoimTitle() + "에   " + p.getName() + "님이 탈퇴하셨습니다:(";
		String newOne=message.getMoimTitle()+"를 탈퇴하셨습니다:(";

		notiService.sendNotiToMoimOne(message,moimOne,newOne );
		notiService.sendNotiToPrivate(message, newOne, p.getId());
		
		
		
	}
	
	/**
	 * 모임 강퇴알림 날리기
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 * @Date
	 */
	@MessageMapping("/moim/banNoti")
	@SendTo("/topic/moim")
	public void moimBanNoti(MoimJoinDTO message) throws Exception {
		
		notiService.sendNotiToMoim(message, message.getUserName() + "님이 강퇴당하셨습니다:(");
	}

	/**
	 * 모임 강퇴알림 날리기(개인)
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 * @Date
	 */
	@MessageMapping("/noti/banNoti")
	@SendTo("/topic/noti")
	public void banNoti(MoimJoinDTO message) throws Exception {
		//username을 peopleId로 받아서 강퇴당한 사람 찾기
		People p = peopleRepository.findById(Long.parseLong(message.getUserName())).get();
		message.setUserName(p.getName());
		
		String moimOne=message.getMoimTitle() + "에   " + p.getName() + "님이 강퇴당하셨습니다:(";
		String newOne=message.getMoimTitle()+"에서 강퇴당하셨습니다:(";

		notiService.sendNotiToMoimOne(message,moimOne,newOne );
		notiService.sendNotiToPrivate(message, newOne, p.getId());
		
	}
	/**
	 * 모임 글작성 알림 날리기
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 * @Date
	 */
	@MessageMapping("/moim/writeNoti")
	@SendTo("/topic/moim")
	public void moimwriteNoti(MoimJoinDTO message) throws Exception {
		notiService.sendNotiToMoim(message, message.getUserName() + "님이 계획을 작성하셨습니다.");
	}
	
	/**
	 * 모임 글작성 알림 날리기(개인)
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 * @Date
	 */
	@MessageMapping("/noti/writeNoti")
	@SendTo("/topic/noti")
	public void writeNoti(MoimJoinDTO message) throws Exception {
		long moimNo = Long.parseLong(message.getMoimNo());
		
			Moim moim = moimRepository.findById(moimNo).get();

			String moimOne=moim.getTitle() + "에   " +message.getUserName()+"님이 계획을 작성하셨습니다.";
			String writer=moim.getTitle() + "에  계획을 작성하셨습니다.";

			notiService.sendNotiToMoimOne(message,moimOne,writer );
		
	}
}