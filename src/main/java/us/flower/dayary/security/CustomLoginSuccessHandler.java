package us.flower.dayary.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.servlet.ModelAndView;

import us.flower.dayary.domain.People;
import us.flower.dayary.repository.people.PeopleRepository;
 
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
 
	@Autowired
	PeopleRepository peopleRepository;
	
    public CustomLoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }
    
    /*
     * 시큐리티 컨피그에 로그인해야하는 부분은 여기를탐
     * 인터셉터로 인해 로그인화면 가는 직인데 성공후 작성해야하는 로직
     * 2020-06-10 choiseongjun
     * */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
        Authentication authentication) throws ServletException, IOException {
        
    	
    	//((SecurityMember)authentication.getPrincipal()).setIp(getClientIp(request));
    	String username = authentication.getName();
		People dbPeople = peopleRepository.findByName(username);
        HttpSession session = request.getSession();
        session.setAttribute("peopleId", dbPeople.getId());// NO세션저장
		session.setAttribute("peopleName", dbPeople.getName());// 이름세션저장
		session.setAttribute("peopleEmail", dbPeople.getEmail());// ID세션저장
		session.setAttribute("people",dbPeople);
		
        String redirectUrl = (String) session.getAttribute("savePage");
        if (dbPeople.getActivation().equals("Y")&&dbPeople.getDelete_yn()=='N') {	//회원 인증이 되었고 탈퇴회원이 아닐경우
	        
        	if (redirectUrl != null) {
	        	
	            session.removeAttribute("savePage");
	            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	        } else {
	            super.onAuthenticationSuccess(request, response, authentication);
	        }
        	
        }else if(dbPeople.getDelete_yn()=='Y') {							//탈퇴회원일경우
			redirectUrl= "/deletedPeople";
			getRedirectStrategy().sendRedirect(request, response, redirectUrl);
		} else {
			redirectUrl= "/authlogout";
			getRedirectStrategy().sendRedirect(request, response, redirectUrl);
		}
    }
    
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("Proxy-Client-IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("WL-Proxy-Client-IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("HTTP_CLIENT_IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getHeader("HTTP_X_FORWARDED_FOR");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = request.getRemoteAddr();
         }
         return ip;
    }
}


