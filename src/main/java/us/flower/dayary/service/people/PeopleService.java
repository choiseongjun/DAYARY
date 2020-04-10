package us.flower.dayary.service.people;

import javax.mail.MessagingException;

import us.flower.dayary.domain.People;

public interface PeopleService {
	
	public String sendAuthUrlMail(People people) throws  MessagingException;
	public String sendAuthFindPassWordMail(People people) throws  MessagingException;
	
}
