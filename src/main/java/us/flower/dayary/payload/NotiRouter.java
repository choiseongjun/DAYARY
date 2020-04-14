package us.flower.dayary.payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.Noti;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.DTO.MoimJoinDTO;
import us.flower.dayary.repository.noti.NotiRepository;
import us.flower.dayary.repository.people.PeopleRepository;

@Controller
public class NotiRouter {

	@Autowired
	NotiRepository notifyRepository;
	@Autowired
	PeopleRepository peopleRepository;

	@MessageMapping("/noti/joinNoti")
	@SendTo("/topic/noti")
	public void noti(MoimJoinDTO message) throws Exception {
		// 모임가입시 모임원에게 개인 알림
		String moimPeopleList = message.getMoimPeopleList();
		String[] moimPeopleListstr = moimPeopleList.split(",");
		int moimNo = Integer.parseInt(message.getMoimNo());
		String moimtitle = message.getMoimTitle();
		String userName = message.getUserName();
		
		Moim moim = new Moim();
		moim.setId(moimNo);
		
		Noti noti = new Noti();// 알림객체를 들고온다
		
		noti.setGubunCd('P');
		noti.setMoim(moim);
		noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
		
		for (int i = 0; i < moimPeopleListstr.length; i++) {
			int notipeople = Integer.parseInt(moimPeopleListstr[i]);// 알림받을유저
			People people = new People();
			people.setId(notipeople);
			noti.setPeople(people);
			noti.setMemo(moimtitle + "에   " + userName + "님이  가입하셨습니다:)");
		
			notifyRepository.save(noti);

		}

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
	public MoimJoinDTO moimNoti(MoimJoinDTO message) throws Exception {
		// 모입가입시 팀내알림

		int moimNo = Integer.parseInt(message.getMoimNo());
		String userName = message.getUserName();

		Moim moim = new Moim();
		moim.setId(moimNo);

		Noti noti = new Noti();// 알림객체를 들고온다
		noti.setMemo(userName + "님이  가입하셨습니다!");
		noti.setMoim(moim);
		// 모임알림
		noti.setGubunCd('M');
		noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
		notifyRepository.save(noti);

		return message;

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
		Noti noti = new Noti();// 알림객체를 들고온다

		Moim moim = new Moim();
		moim.setId(Integer.parseInt(message.getMoimNo()));
		noti.setMoim(moim);
		noti.setPeople(moim.getPeople());
		noti.setGubunCd('P');
		noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
		noti.setMemo(message.getMoimTitle() + "에 " + message.getUserName() + "님의 가입 요청을 승인해주세요:)");
		notifyRepository.save(noti);
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

		Noti noti = new Noti();// 알림객체를 들고온다

		Moim moim = new Moim();
		moim.setId(Integer.parseInt(message.getMoimNo()));
		noti.setMoim(moim);
		noti.setGubunCd('M');
		noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
		noti.setMemo(message.getUserName() + "님이 탈퇴하셨습니다:(");
		notifyRepository.save(noti);
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

		String[] moimPeopleListstr = message.getMoimPeopleList().split(",");
		int moimNo = Integer.parseInt(message.getMoimNo());
		String moimtitle = message.getMoimTitle();

		//username을 peopleId로 받아서 강퇴당한 사람 찾기
				People p = peopleRepository.findById(Long.parseLong(message.getUserName())).get();
				Moim moim = new Moim();
				moim.setId(moimNo);
				
				//강퇴당한사람에게 알림
				// private 알림
				Noti noti = new Noti();// 알림객체를 들고온다
				noti.setPeople(p);
				noti.setMemo(moimtitle + "를 탈퇴하셨습니다:(");
				noti.setGubunCd('P');
				noti.setMoim(moim);
				noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
				notifyRepository.save(noti);
		
		for (int i = 0; i < moimPeopleListstr.length; i++) {

			int notipeople = Integer.parseInt(moimPeopleListstr[i]);// 알림받을유저
			People people = new People();
			people.setId(notipeople);

			
			noti.setPeople(people);
			noti.setMemo(moimtitle + "에   " + message.getUserName() + "님이 탈퇴하셨습니다:(");
		
			notifyRepository.save(noti);
		}
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

		Noti noti = new Noti();// 알림객체를 들고온다

		Moim moim = new Moim();
		moim.setId(Integer.parseInt(message.getMoimNo()));
		noti.setMoim(moim);
		noti.setGubunCd('M');
		noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
		noti.setMemo(message.getUserName() + "님이 강퇴당하셨습니다:(");
		notifyRepository.save(noti);
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
		int moimNo = Integer.parseInt(message.getMoimNo());
		String moimtitle = message.getMoimTitle();
		String[] moimPeopleListstr = message.getMoimPeopleList().split(",");
		//username을 peopleId로 받아서 강퇴당한 사람 찾기
		People p = peopleRepository.findById(Long.parseLong(message.getUserName())).get();
		Moim moim = new Moim();
		moim.setId(moimNo);
		
		//강퇴당한사람에게 알림
		// private 알림
		Noti noti = new Noti();// 알림객체를 들고온다
		noti.setPeople(p);
		noti.setMemo(moimtitle + "에서  강퇴당하셨습니다:(");
		noti.setGubunCd('P');
		noti.setMoim(moim);
		noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
		notifyRepository.save(noti);
		
		for (int i = 0; i < moimPeopleListstr.length; i++) {

			int notipeople = Integer.parseInt(moimPeopleListstr[i]);// 알림받을유저


			People people = new People();
			people.setId(notipeople);

			noti.setPeople(people);
			noti.setMemo(moimtitle + "에   " +p.getName()+"님이 강퇴당하셨습니다:(");
		
			notifyRepository.save(noti);
		}
		
	}
}