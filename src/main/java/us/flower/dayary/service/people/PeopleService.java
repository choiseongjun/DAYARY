package us.flower.dayary.service.people;

import java.util.List;

import javax.mail.MessagingException;

import us.flower.dayary.config.social.connection.UserConnection;
import us.flower.dayary.domain.People;

public interface PeopleService {
	
	public String sendAuthUrlMail(People people) throws  MessagingException;
	public String sendAuthFindPassWordMail(People people) throws  MessagingException;
	public List<People> selectAll();
	public boolean isExistUser(UserConnection userConnection);
	public People findBySocial(UserConnection userConnection);
	public People signUp(UserConnection userConnection);
	
}
