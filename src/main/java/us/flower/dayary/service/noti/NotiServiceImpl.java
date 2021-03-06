package us.flower.dayary.service.noti;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.Noti;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.DTO.MoimJoinDTO;
import us.flower.dayary.repository.moim.MoimPeopleRepository;
import us.flower.dayary.repository.moim.MoimRepository;
import us.flower.dayary.repository.noti.NotiRepository;
import us.flower.dayary.repository.people.PeopleRepository;

@Service
@Transactional
public class NotiServiceImpl implements NotiService {

	@Autowired
	NotiRepository notifyRepository;
	@Autowired
	PeopleRepository peopleRepository;
	@Autowired
	MoimRepository moimRepository;
	@Autowired
	MoimPeopleRepository moimPeopleRepository;

	@Override
	public Noti sendNotiToMoim(MoimJoinDTO message, String msg) {
		// TODO Auto-generated method stub
		Noti noti = new Noti();// 알림객체를 들고온다
		noti.setGubunCd('M');
		noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
		noti.setMemo(msg);
		
		Moim moim = new Moim();
		moim.setId(Long.parseLong(message.getMoimNo()));
		noti.setMoim(moim);
		
		notifyRepository.save(noti);
		return noti;
		
	}
	/**
	 * 모임가입시 모임원에게 개인 알림
	 *
	 * @param Dto,알림글,해당조건시알림
	 * @return
	 * @throws @author JY
	 * message의 모임 아이디를 통해 모임가입자확인
	 *  해당하는 사람의 경우 다른 메세지를 보내야할때 username/people.name과 같을경우 msg2전송
	 * 
	 */
	@Override
	public Noti sendNotiToMoimOne(MoimJoinDTO message, String msg,String msg2) {
		// TODO Auto-generated method stub
		long moimNo = Long.parseLong(message.getMoimNo());

		Moim moim = new Moim();
		moim.setId(moimNo);
		
		List<MoimPeople> moimPeopleList = moimPeopleRepository.findByMoim_id(moimNo);
		Date time=new java.sql.Date(System.currentTimeMillis());
		
		for (int i = 0; i < moimPeopleList.size(); i++) {
			Noti noti = new Noti();// 알림객체를 들고온다
			noti.setGubunCd('P');
			noti.setMoim(moim);
			noti.setCreateDate(time);
			//알림보낼사람설정
			People p=new People();
			p.setId(moimPeopleList.get(i).getPeople().getId());
			noti.setPeople(p);
			
			if(p.getId()==peopleRepository.findByName(message.getUserName()).getId())
				noti.setMemo(msg2);
			else
				noti.setMemo(msg);
			notifyRepository.save(noti);

		}
		Noti noti = new Noti();
		noti.setMoim(moim);
		noti.setCreateDate(time);
		noti.setMemo(msg);
		noti.setGubunCd('P');
		return noti;
	
	}
	
	@Override
	public Noti sendNotiToPrivate(MoimJoinDTO message, String msg,Long peopleId) {
		// TODO Auto-generated method stub
		Noti noti = new Noti();// 알림객체를 들고온다
		noti.setGubunCd('P');
		noti.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
		noti.setMemo(msg);
		
		Moim moim = new Moim();
		moim.setId(Long.parseLong(message.getMoimNo()));
		noti.setMoim(moim);

		People p=new People();
		p.setId(peopleId);
		noti.setPeople(p);
		
		notifyRepository.save(noti);
		return noti;
	}

	@Override
	public int totalNoti(People p) {
		// TODO Auto-generated method stub
		return notifyRepository.countByPeople_idAndGubunCdAndCreateDateBetween(p.getId(),'P',Timestamp.valueOf(p.getUpdatedAt()),Timestamp.valueOf(LocalDateTime.now()));
	}

	/*
	 * @Override public List<Noti> getPrivateNoti(long peopleId, int start) { //
	 * TODO Auto-generated method stub return
	 * notifyRepository.GetPrivateNoti(peopleId, start, start+9); }
	 * 
	 * @Override public List<Noti> getMoimNoti(long moimId, int start) { // TODO
	 * Auto-generated method stub return notifyRepository.GetMoimNoti(moimId, start,
	 * start+9); }
	 */
	@Override
	public List<Noti> getMoreNoti(int pageNum, long id, char cd) {
		// TODO Auto-generated method stub
		int page = (pageNum == 0) ? 0 : (pageNum - 1); // page는 index 처럼 0부터 시작
		Pageable pageable = PageRequest.of(page, 50, Sort.Direction.DESC, "id");// 내림차순으로 정렬한다

		return notifyRepository.findByPeople_idAndGubunCd(pageable, id, cd);
	}
	@Override
	public List<Noti> getMoimNoti(int pageNum, long id, char cd) {
		// TODO Auto-generated method stub
		int page = (pageNum == 0) ? 0 : (pageNum - 1); // page는 index 처럼 0부터 시작
		Pageable pageable = PageRequest.of(page, 50, Sort.Direction.DESC, "id");// 내림차순으로 정렬한다
		
		return notifyRepository.findByMoim_idAndGubunCd(pageable, id, cd);
	}


}
