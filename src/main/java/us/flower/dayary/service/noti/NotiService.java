package us.flower.dayary.service.noti;


import java.util.List;

import us.flower.dayary.domain.Noti;
import us.flower.dayary.domain.DTO.MoimJoinDTO;

public interface NotiService {
	public void sendNotiToMoimOne(MoimJoinDTO message,String msg,String msg2);
	public void sendNotiToPrivate(MoimJoinDTO message,String msg,Long peopleId);
	public void sendNotiToMoim(MoimJoinDTO message,String msg);
	List<Noti> getMoreNoti(int pageNum,long id,char cd);
	int totalNoti(long id);
	
}
