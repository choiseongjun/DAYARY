package us.flower.dayary.service.people;

import java.util.List;
import java.util.Random;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import us.flower.dayary.config.social.connection.UserConnection;
import us.flower.dayary.domain.People;
import us.flower.dayary.repository.people.PeopleRepository;
@Service
public class PeopleServiceImpl implements PeopleService {
	 @Autowired
	 private JavaMailSender mailSender;
	 @Autowired
	 private PeopleRepository peopleRepository;
	 
		// 이메일 난수 만드는 메서드
		private String init() {
			Random ran = new Random();
			StringBuffer sb = new StringBuffer();
			int num = 0;
	
			do {
				num = ran.nextInt(75) + 48;
				if ((num >= 48 && num <= 57) || (num >= 65 && num <= 90) || (num >= 97 && num <= 122)) {
					sb.append((char) num);
				} else {
					continue;
				}
	
			} while (sb.length() < size);
			if (lowerCheck) {
				return sb.toString().toLowerCase();
			}
			return sb.toString();
		}
	
		// 난수를 이용한 키 생성
		private boolean lowerCheck;
		private int size;
	
		public String getKey(boolean lowerCheck, int size) {
			this.lowerCheck = lowerCheck;
			this.size = size;
			return init();
		}
	 
	 
	 @Override
	   public String sendAuthUrlMail(People people) throws  MessagingException{
		   String key= getKey(false,15);
		   MimeMessage message=mailSender.createMimeMessage();
		   try {
			   message.setFrom(new InternetAddress("choisjjunjun2702@gmail.com"));
			   message.setSubject("[본인인증]Fruit인증메일","utf-8");
			   message.setContent("안녕하세요.<hr>"+people.getName()+"님 사이트 이용을 위해 히단의 url을 눌러 인증을 진행하세요. <hr><a href='http://www.fruitteams.com/auth/"+key+"/"+people.getEmail()+"'>메일인증</a>", "text/html;charset=utf-8");
			   message.addRecipient(RecipientType.TO,new InternetAddress(people.getEmail()));
			   mailSender.send(message);
			   return key;
		   } catch (MessagingException e) {
			   // TODO Auto-generated catch block
			   throw e;
		   }
	   }

		@Override
		public String sendAuthFindPassWordMail(People people) throws MessagingException {
			// TODO Auto-generated method stub
			String key= getKey(false,6);
			   MimeMessage message=mailSender.createMimeMessage();
			   try {
				   message.setFrom(new InternetAddress("choisjjunjun2702@gmail.com"));
				   message.setSubject("[임시비밀번호]Fruit임시비밀번호메일","utf-8");
				   message.setContent("<h2>안녕하세요</h2><h3>"+people.getName()+"님</h3> 해당임시비밀번호로 로그인하세요.<hr> 임시비밀번호:"+key,"text/html;charset=utf-8");
				   message.addRecipient(RecipientType.TO,new InternetAddress(people.getEmail()));
				   mailSender.send(message);
				   return key;
			   } catch (MessagingException e) {
				   // TODO Auto-generated catch block
				   throw e;
			   }
		}

		@Override
		public List<People> selectAll() {
			return peopleRepository.findAll();
		}


		@Override
		public boolean isExistUser(UserConnection userConnection) {
			final People user = peopleRepository.findBySocial(userConnection);
	        return (user != null);
		}


		@Override
		public People findBySocial(UserConnection userConnection) {
			final People user = peopleRepository.findBySocial(userConnection);
	        if (user == null) throw new RuntimeException();
	        return user;
		}


		@Override
		public People signUp(UserConnection userConnection) {
			 final People user = People.signUp(userConnection);
		     return peopleRepository.save(user);
		}


}
