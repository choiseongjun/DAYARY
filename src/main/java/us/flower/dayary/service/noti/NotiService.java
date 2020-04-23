package us.flower.dayary.service.noti;


import java.util.List;

import us.flower.dayary.domain.Noti;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.DTO.MoimJoinDTO;

public interface NotiService {
	public Noti sendNotiToMoimOne(MoimJoinDTO message,String msg,String msg2);
	public Noti sendNotiToPrivate(MoimJoinDTO message,String msg,Long peopleId);
	public Noti sendNotiToMoim(MoimJoinDTO message,String msg);
	List<Noti> getMoreNoti(int pageNum,long id,char cd);
	int totalNoti(People p);
	
}
