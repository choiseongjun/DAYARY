package us.flower.dayary.config.aop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;

import com.google.common.base.Joiner;

import us.flower.dayary.domain.People;
import us.flower.dayary.domain.UserHistory;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.repository.people.UserHistoryRepository;

@Component // 1
@Aspect // 2
public class RequestLoggingAspect {
	private static final Logger logger = LoggerFactory.getLogger(RequestLoggingAspect.class);

	@Autowired
	private PeopleRepository peopleRepository;
	@Autowired
	private UserHistoryRepository userHistoryRepository;
	
	private String paramMapToString(Map<String, String[]> paramMap) {
		return paramMap.entrySet().stream()
				.map(entry -> String.format("%s -> (%s)", entry.getKey(), Joiner.on(",").join(entry.getValue())))
				.collect(Collectors.joining(", "));
	}

	@Pointcut("within(us.flower.dayary.controller..*)") // 3
	public void onRequest() {
	}

	@Around("us.flower.dayary.config.aop.RequestLoggingAspect.onRequest()") // 4
	public Object doLogging(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest request = // 5
				((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		Map<String, String[]> paramMap = request.getParameterMap();
		String params = "";
		if (paramMap.isEmpty() == false) {
			params = " [" + paramMapToString(paramMap) + "]";
		}

		long start = System.currentTimeMillis();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		     String username = auth.getName();
//		     People people = peopleRepository.findByName(username);
//		     long userid = people.getId();
//		     
//			System.out.println("프린서팔"+userid);
		     String anony = "";
		     long userid = 0;
		     People people = peopleRepository.findByName(username);
		     
		     SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
		     String lastTime = formatter.format ( request.getSession().getLastAccessedTime() );//세션마지막요청시간
		     String initTime = formatter.format ( request.getSession().getCreationTime());//세션초기시간
		    
		     UrlPathHelper urlPathHelper = new UrlPathHelper();
		     String accessPath = urlPathHelper.getOriginatingRequestUri(request);
			 System.out.println("originalURL-->" + accessPath);
		     
		     UserHistory userhistory = new UserHistory();
		    
		     if(!(accessPath.equals("/Menulist"))) {
		    	 userhistory.setAccesspath(accessPath);			//메뉴접근	 
		    	 userhistory.setAccessname(username);				//유저이름
			     userhistory.setIpaddress(request.getRemoteAddr());//아이피주소
			     
			     userhistory.setLogindate(initTime);			//초기접속시간
			     userhistory.setSessionlastaccess(lastTime);	//마지막접속시간
			     userhistory.setOstype(request.getHeader("User-Agent"));
		    	 if(!(username.equals("anonymousUser"))) {
					     People peoples=new People();
					     userid = people.getId();
					     peoples.setId(userid);
					     userhistory.setPeople(people);
				 }
		         userHistoryRepository.save(userhistory);
		     }
		  
		     
		   
		
		     
			
			return pjp.proceed(pjp.getArgs()); // 6
		} finally {
			long end = System.currentTimeMillis();
			logger.debug("Request: {} {}{} < {} ({}ms)", request.getMethod(), request.getRequestURI(), params,
					request.getRemoteHost(), end - start);
		}
	}
}