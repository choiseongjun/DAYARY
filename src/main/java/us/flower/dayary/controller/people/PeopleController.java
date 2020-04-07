package us.flower.dayary.controller.people;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import us.flower.dayary.repository.CommonRepository;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.repository.people.RoleRepository;
import us.flower.dayary.security.JwtTokenProvider;
import us.flower.dayary.service.people.PeopleService;

@Controller
public class PeopleController {
	/* NaverLoginBO */
	private NaverLoginBO naverLoginBO;
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
	CommonRepository commonRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PeopleService service;

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
	public ModelAndView needLogin() throws Exception {
		ModelAndView mav = new ModelAndView("/people/signinwarning");

		return mav;
	}

	@GetMapping("/loginSuccess")
	public String loginSuccess(People people, HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Authentication authentication, ModelAndView mav) throws ServletException {
		String username = authentication.getName();
		People dbPeople = peopleRepository.findByName(username);
//			session.setAttribute("peopleId", dbPeople.getId());// NO세션저장
//			session.setAttribute("peopleName", dbPeople.getName());// 이름세션저장
//			session.setAttribute("peopleEmail", dbPeople.getEmail());// ID세션저장
//			session.setAttribute("people",dbPeople);
		if (request.isUserInRole("ROLE_ADMIN")) {
			return "redirect:/admin/admini";
			// mav.setViewName("redirect:/admin/admini");
		} else {
			if (dbPeople.getActivation().equals("Y")) {
				return "redirect:/";
			} else {
				return "redirect:/authlogout";
			}
		}

//			String savePage = (String)session.getAttribute("savePage");
//			if(savePage!=null) {
//				mav.setViewName("redirect:/"+savePage);
//				session.setAttribute("savePage", null);
//			}
	}

	@PostMapping("/signin")
	@ResponseBody
	public Map<String, Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpSession session,
			Model model, ModelAndView mav, Principal principal) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		// Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtTokenProvider.generateToken(authentication);
		try {
			if (peopleRepository.existsByEmail(loginRequest.getEmail())) {
				People dbPeople = peopleRepository.findByEmail(loginRequest.getEmail());
				if (bcrypt.checkpw(loginRequest.getPassword(), dbPeople.getPassword())) {// 비밀번호가맞다면
					if (!dbPeople.getActivation().equals("Y")) {
						returnData.put("code", "E4024");
						returnData.put("message", "이메일인증을 완료해주세요:(");
						return returnData;
					}

					session.setAttribute("peopleId", dbPeople.getId());// NO세션저장
					session.setAttribute("peopleName", dbPeople.getName());// 이름세션저장
					session.setAttribute("peopleEmail", dbPeople.getEmail());// ID세션저장
					session.setAttribute("people", dbPeople);
					returnData.put("people", dbPeople);
					returnData.put("code", "1");
					String jwt = tokenProvider.generateToken(authentication);
					JwtAuthenticationResponse csj = new JwtAuthenticationResponse(jwt);
					model.addAttribute("csj", csj);
					String savePage = (String) session.getAttribute("savePage");
//					    	RoleName rolename = null;
//					    	Role roles =new Role();
//					    	roles.setName(rolename.ADMIN);
//					    	
//					    	Set<Role> set =new HashSet<Role>();
//					    	set.add(roles);
//					    	
//					    	dbPeople.setRoles(set);

					if (savePage != null) {
						mav.setViewName("redirect:/" + savePage);
						session.setAttribute("savePage", null);
						returnData.put("savePage", savePage);
						returnData.put("code", "2");
					}
					returnData.put("message", "로그인 완료!");
					returnData.put("jwt", new JwtAuthenticationResponse(jwt));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnData.put("code", "E4024");
			returnData.put("message", "잠시 후, 다시 시도해주세요:(");
		}

		return returnData;
	}

	@GetMapping("/auth/checkName/{name}")
	@ResponseBody
	public Map<String, Object> checkName(@PathVariable("name") String name) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		if (peopleRepository.existsByName(name)) {
			returnData.put("message", "이미 존재하는 닉네임입니다.변경해주세요:(");
			returnData.put("code", "true");
		}
		else
			returnData.put("code", "false");
			
		return returnData;
	}
	@GetMapping("/auth/checkEmail/{email}")
	@ResponseBody
	public Map<String, Object> checkMail(@PathVariable("email") String email) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		if (peopleRepository.existsByEmail(email)) {
			
			returnData.put("message", "이미 가입된 메일입니다:)");
			returnData.put("code", "true");
		}
		else
			returnData.put("code", "false");
		
		return returnData;
	}

	@PostMapping("/signup")
	public String registerUser(@Valid @ModelAttribute SignUpRequest signUpRequest, Model model) {
		System.out.println(signUpRequest);
		try {

				// Creating user's account
				People user = new People(signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getName(),
						signUpRequest.getPhoto(), signUpRequest.getActivation(), signUpRequest.getJob(),
						signUpRequest.getSex(), signUpRequest.getInterests());

				user.setPassword(bcrypt.hashpw(user.getPassword()));

				Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
						.orElseThrow(() -> new AppException("먼저 Role테이블에 insert문으로 데이터 넣어주세요.트렐로에 필수 INSERT문 넣었습니다"));

				user.setRoles(Collections.singleton(userRole));

				/*
				 * if (file != null) { // 파일이 null이 아니면 // 이미지파일이름생성 String imageName = "";
				 * while (true) { imageName = tokenGenerator.getToken(); // DB에 파일이름이 존재하지 않으면
				 * moim domain에 set if (!peopleRepository.existsByImageName(imageName)) {
				 * user.setImageName(imageName); break; } }
				 * 
				 * // 이미지파일확장자추출 String originalFileName = file.getOriginalFilename(); String
				 * fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")
				 * + 1) .toLowerCase(); user.setImageExtension(fileExtension);
				 * 
				 * // 파일업로드 try { fileManager.fileUpload(file, moimImagePath + "/" + imageName +
				 * "." + fileExtension); } catch (IOException e) { e.printStackTrace(); } }
				 */
				String key = service.sendAuthUrlMail(user);
				user.setActivation(key);
				People result = peopleRepository.save(user);
				URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{username}")
						.buildAndExpand(result.getId()).toUri();
				model.addAttribute("message", "회원가입이 완료되었습니다. 인증메일을 확인하고 로그인하세요:)");
				
		} catch (Exception e) {
			model.addAttribute("message", "잠시 후, 다시 시도해주세요:(");
		}
		model.addAttribute("code", "true");
		model.addAttribute("interests", commonRepository.findByCommHead("CA1"));
		return "people/signin";
	}

	/**
	 * 인증메일
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 */
	@GetMapping("/auth/{key}/{email}")
	public String authUrl(@PathVariable("key") String key, @PathVariable("email") String email, Model model) {

		People p = peopleRepository.findByEmail(email);
		if (p.getActivation().equals(key)) {
			// 인증키 일치시 activate
			p.setActivation("Y");
			peopleRepository.save(p);
			model.addAttribute("authMessage", "인증되었습니다.");

		} else if (p.getActivation().contentEquals("Y")) {
			model.addAttribute("authMessage", "로그인하여 이용하세요.");
		} else {
			model.addAttribute("authMessage", "잘못된접근입니다.");
		}
		model.addAttribute("interests", commonRepository.findByCommHead("CA1"));
		return "people/signin";
	}

	/**
	 * 비밀번호메일
	 *
	 * @param
	 * @return
	 * @throws @author JY
	 */
	@GetMapping("/auth/findPassword/{email}")
	@ResponseBody
	public Map<String, Object> authFindPassword(@PathVariable("email") String email) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			People p = peopleRepository.findByEmail(email);
			if (p == null) {

				returnData.put("code", "2");
				returnData.put("message", "존재하지않는 회원입니다:)");
				return returnData;
			}
			// 임시비밀번호 메일전송
			String pass = service.sendAuthFindPassWordMail(p);
			// 비밀번호 바꿔저장
			p.setPassword(bcrypt.hashpw(pass));
			peopleRepository.save(p);

			returnData.put("code", "1");
			returnData.put("message", "메일을 확인해주세요:)");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public String signinView(Model model, HttpSession session, HttpServletRequest request) {
		/* 네이버아이디로 인증 URL을 생성하기 위하여 naverLoginBO클래스의 getAuthorizationUrl메소드 호출 */
		String naverAuthUrl = naverLoginBO.getAuthorizationUrl(session);
		// https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=sE***************&
		// redirect_uri=http%3A%2F%2F211.63.89.90%3A8090%2Flogin_project%2Fcallback&state=e68c269c-5ba9-4c31-85da-54c16c658125
		// 네이버
		model.addAttribute("message", request.getServletContext());
		String referrer = request.getHeader("Referer");
		request.getSession().setAttribute("prevPage", referrer);
		model.addAttribute("interests", commonRepository.findByCommHead("CA1"));
		model.addAttribute("menu5_hover", "on");

		return "people/signin";
	}

	/*
	 * 이메일 인증을 받지않았을떄 이쪽을 탄다
	 */
	@GetMapping("/authlogout")
	public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
		HttpSession session = request.getSession(false);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		session.invalidate();

		SecurityContextHolder.clearContext();

		model.addAttribute("AuthError", true);
		return "people/authFail";
	}

	// Login form with error
	@RequestMapping("/loginerror")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		model.addAttribute("interests", commonRepository.findByCommHead("CA1"));
		return "people/signin";
	}

	// 비밀번호변경
	@PostMapping("/auth/changePassword")
	@ResponseBody
	public Map<String, Object> changePassword(@RequestBody Map<String, Object> param) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		long peopleId = Long.parseLong(param.get("peopleId").toString());
		People p = peopleRepository.findById(peopleId).get();

		p.setPassword(bcrypt.hashpw(param.get("change").toString()));
		peopleRepository.save(p);
		returnData.put("message", "비밀번호가 변경되었습니다.");

		return returnData;
	}

}
