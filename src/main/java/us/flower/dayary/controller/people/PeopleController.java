package us.flower.dayary.controller.people;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import us.flower.dayary.common.BCRYPT;
import us.flower.dayary.common.FileManager;
import us.flower.dayary.common.TokenGenerator;
import us.flower.dayary.config.NaverLoginBO;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.Role;
import us.flower.dayary.domain.RoleName;
import us.flower.dayary.exception.AppException;
import us.flower.dayary.payload.JwtAuthenticationResponse;
import us.flower.dayary.payload.LoginRequest;
import us.flower.dayary.payload.SignUpRequest;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.repository.people.RoleRepository;
import us.flower.dayary.security.CustomLoginSuccessHandler;
import us.flower.dayary.security.JwtTokenProvider;

@Controller
public class PeopleController {
	/* NaverLoginBO */
	private  NaverLoginBO naverLoginBO;
	private String apiResult = null;
	@Autowired
	private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
	this.naverLoginBO = naverLoginBO;
	}

	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	JwtTokenProvider tokenProvider;
	@Autowired
	PeopleRepository peopleRepository;
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
    private TokenGenerator tokenGenerator;
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	private FileManager fileManager;
	@Value("${moimImagePath}")
	private String moimImagePath;

	private BCRYPT bcrypt;

	public PeopleController(BCRYPT bcrypt) {
		this.bcrypt = bcrypt;
	}

	
	@RequestMapping("/people/signinWarning")
	public ModelAndView needLogin() throws Exception{
		ModelAndView mav=new ModelAndView("/people/signinwarning");
		
		return mav;
	}
	
	@GetMapping("/loginSuccess")
	public String loginSuccess(People people,HttpSession session,HttpServletRequest request
			,HttpServletResponse response,Authentication authentication,ModelAndView mav) throws ServletException {
			String username = authentication.getName();
			People dbPeople = peopleRepository.findByName(username);
			System.out.println("SFSDFSDFDFSDFSDFSDFSDFSD");
			session.setAttribute("peopleId", dbPeople.getId());// NO세션저장
			session.setAttribute("peopleName", dbPeople.getName());// 이름세션저장
			session.setAttribute("peopleEmail", dbPeople.getEmail());// ID세션저장
			session.setAttribute("people",dbPeople);
			if(request.isUserInRole("ROLE_ADMIN")) {
		    	return "redirect:/admin/admini";
		    	//mav.setViewName("redirect:/admin/admini");
		    }
//			String savePage = (String)session.getAttribute("savePage");
//			if(savePage!=null) {
//				mav.setViewName("redirect:/"+savePage);
//				session.setAttribute("savePage", null);
//			}
		return "redirect:/";
	}
	@PostMapping("/signin")
	@ResponseBody
	public Map<String, Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,HttpSession session
												,Model model,ModelAndView mav
												,Principal principal) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(authentication);
		 String token = jwtTokenProvider.generateToken(authentication);
        try {
			if (peopleRepository.existsByEmail(loginRequest.getEmail())) {
				People dbPeople = peopleRepository.findByEmail(loginRequest.getEmail());
					if (bcrypt.checkpw(loginRequest.getPassword(), dbPeople.getPassword())) {// 비밀번호가맞다면
				        
	
						session.setAttribute("peopleId", dbPeople.getId());// NO세션저장
						session.setAttribute("peopleName", dbPeople.getName());// 이름세션저장
						session.setAttribute("peopleEmail", dbPeople.getEmail());// ID세션저장
						session.setAttribute("people",dbPeople);
						returnData.put("people",dbPeople);
						returnData.put("code", "1");
							String jwt = tokenProvider.generateToken(authentication);
							JwtAuthenticationResponse csj= new JwtAuthenticationResponse(jwt);
					        model.addAttribute("csj",csj);
					    	String savePage = (String)session.getAttribute("savePage");
//					    	RoleName rolename = null;
//					    	Role roles =new Role();
//					    	roles.setName(rolename.ADMIN);
//					    	
//					    	Set<Role> set =new HashSet<Role>();
//					    	set.add(roles);
//					    	
//					    	dbPeople.setRoles(set);
					    	
					        if(savePage!=null) {
								mav.setViewName("redirect:/"+savePage);
								session.setAttribute("savePage", null);
								returnData.put("savePage",savePage);
								returnData.put("code", "2");								
							}
						returnData.put("message", "로그인 완료!");
						returnData.put("jwt",new JwtAuthenticationResponse(jwt));
					} 
			} 
		} catch (Exception e) {
			e.printStackTrace();
			returnData.put("code", "E4024");
			returnData.put("message", "잠시 후, 다시 시도해주세요:(");
		}
      
		
		return returnData;
	}
	
	@PostMapping("/signup")
	@ResponseBody
	public Map<String, Object> registerUser(@Valid @RequestPart("signUpRequest") SignUpRequest signUpRequest, @RequestPart(name="file",required=false) MultipartFile file) {
 
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			if(!peopleRepository.existsByEmail(signUpRequest.getEmail())) {
				 if(peopleRepository.existsByName(signUpRequest.getName())) {
						returnData.put("code", "3");
						returnData.put("message", "이미 존재하는 닉네임입니다.변경해주세요:(");
						
				}else {
						// Creating user's account
						People user = new People(signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getName(),
								signUpRequest.getPhoto(), signUpRequest.getActivation());
		 
						user.setPassword(bcrypt.hashpw(user.getPassword()));
		
						Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
								.orElseThrow(() -> new AppException("먼저 Role테이블에 insert문으로 데이터 넣어주세요.트렐로에 필수 INSERT문 넣었습니다"));
		
						user.setRoles(Collections.singleton(userRole));
		
						if(file!=null) {	//파일이 null이 아니면 
									//이미지파일이름생성
							        String imageName="";
									while(true){
							        	imageName=tokenGenerator.getToken();
										//DB에 파일이름이 존재하지 않으면 moim domain에 set
							        	if(!peopleRepository.existsByImageName(imageName)){
											user.setImageName(imageName);
											break; 
										} 
									}
							  
									
							        //이미지파일확장자추출
							        String originalFileName = file.getOriginalFilename();
							        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
							        user.setImageExtension(fileExtension);
					
							        //파일업로드
							        try { 
							            fileManager.fileUpload(file, moimImagePath+"/"+imageName+"."+fileExtension);
							        } catch (IOException e) {
							            e.printStackTrace();
							        }
						}
								
						People result = peopleRepository.save(user);
		
						URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{username}")
								.buildAndExpand(result.getId()).toUri();
						returnData.put("code", "1");
						returnData.put("message", "가입완료:)");
				}
			}else {
				returnData.put("code", "2");
				returnData.put("message", "이미 가입 된 아이디입니다:(");
			}
		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "잠시 후, 다시 시도해주세요:(");
		}
		return returnData;
	}

	/**
	 * 회원가입 뷰
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@GetMapping("/signupView")
	public String signupView() {
		return "people/signup";
	}

	/**
	 * 로그인 뷰
	 *
	 * @param
	 * @return
	 * @throws @author choiseongjun
	 */
	@GetMapping("/signinView")
	public String signinView(Model model, HttpSession session,HttpServletRequest request) {
		/* 네이버아이디로 인증 URL을 생성하기 위하여 naverLoginBO클래스의 getAuthorizationUrl메소드 호출 */
		String naverAuthUrl = naverLoginBO.getAuthorizationUrl(session);
		//https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=sE***************&
		//redirect_uri=http%3A%2F%2F211.63.89.90%3A8090%2Flogin_project%2Fcallback&state=e68c269c-5ba9-4c31-85da-54c16c658125
		//네이버
		model.addAttribute("message",request.getServletContext());
		String referrer = request.getHeader("Referer"); 
		request.getSession().setAttribute("prevPage", referrer);



		return "people/signin";
	}

	


	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return "main";
	}
	 // Login form with error
	@RequestMapping("/loginerror")
	public String loginError(Model model) {
	    model.addAttribute("loginError", true);
	    return "people/signin";
	}
}
