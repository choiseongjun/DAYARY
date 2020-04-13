package us.flower.dayary.controller.noti;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.Noti;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.DTO.MoimJoinDTO;
import us.flower.dayary.repository.noti.NotiRepository;

@Controller
public class NotiController {

	@Autowired
	NotiRepository notifyRepository;

	@MessageMapping("/noti/joinNoti")
	@SendTo("/topic/noti")
	public void noti(MoimJoinDTO message) throws Exception {
		// 모임가입시 모임원에게 개인 알림
		String moimPeopleList = message.getMoimPeopleList();
		String[] moimPeopleListstr = moimPeopleList.split(",");

		for (int i = 0; i < moimPeopleListstr.length; i++) {

			String moimNo1 = message.getMoimNo();
			String moimtitle = message.getMoimTitle();
			String userName = message.getUserName();

			System.out.println(moimNo1 + moimtitle + userName);
			System.out.println(moimPeopleListstr[i]);

			int moimNo = Integer.parseInt(moimNo1);// 모임번호
			int notipeople = Integer.parseInt(moimPeopleListstr[i]);// 알림받을유저

			Moim moim = new Moim();
			moim.setId(moimNo);

			People people = new People();
			people.setId(notipeople);

			Noti noti = new Noti();// 알림객체를 들고온다
			noti.setPeople(people);
			noti.setMemo(moimtitle + "에   " + userName + "님이  가입하셨습니다:)");
			// private 알림
			noti.setGubunCd('P');
			noti.setMoim(moim);
			noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
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

		String moimNo1 = message.getMoimNo();
		String userName = message.getUserName();

		int moimNo = Integer.parseInt(moimNo1);// 모임번호

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
		noti.setPeople(moim.getPeople());
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

		for (int i = 0; i < moimPeopleListstr.length; i++) {

			String moimNo1 = message.getMoimNo();
			String moimtitle = message.getMoimTitle();
			String userName = message.getUserName();

			System.out.println(moimNo1 + moimtitle + userName);
			System.out.println(moimPeopleListstr[i]);

			int moimNo = Integer.parseInt(moimNo1);// 모임번호
			int notipeople = Integer.parseInt(moimPeopleListstr[i]);// 알림받을유저

			Moim moim = new Moim();
			moim.setId(moimNo);

			People people = new People();
			people.setId(notipeople);

			Noti noti = new Noti();// 알림객체를 들고온다
			noti.setPeople(people);
			noti.setMemo(moimtitle + "에   " + userName + "님이 탈퇴하셨습니다:(");
			// private 알림
			noti.setGubunCd('P');
			noti.setMoim(moim);
			noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
			notifyRepository.save(noti);
		}
	}
}