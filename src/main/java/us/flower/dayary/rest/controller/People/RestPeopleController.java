package us.flower.dayary.rest.controller.People;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import us.flower.dayary.common.BCRYPT;
import us.flower.dayary.common.FileManager;
import us.flower.dayary.common.TokenGenerator;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.Role;
import us.flower.dayary.domain.RoleName;
import us.flower.dayary.exception.AppException;
import us.flower.dayary.payload.SignUpRequest;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.repository.people.RoleRepository;
import us.flower.dayary.security.JwtTokenProvider;

@RestController
public class RestPeopleController {
	
	@Autowired
	PeopleRepository peopleRepository;
	@Autowired
	JwtTokenProvider tokenProvider;
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
    private TokenGenerator tokenGenerator;
	@Autowired
	private FileManager fileManager;

	private BCRYPT bcrypt;

	public RestPeopleController(BCRYPT bcrypt) {
		this.bcrypt = bcrypt;
	}


	@PostMapping("/rest/signup")
	public Map<String, Object> RestregisterUser(@RequestBody SignUpRequest signUpRequest) {
 
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			if (!peopleRepository.existsByEmail(signUpRequest.getEmail())) {
				// Creating user's account
				People user = new People(0, signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getName(),
						signUpRequest.getPhoto(), signUpRequest.getActivation(), null, null, null, null, null, null);
 
				user.setPassword(bcrypt.hashpw(user.getPassword()));

				Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
						.orElseThrow(() -> new AppException("먼저 Role테이블에 insert문으로 데이터 넣어주세요.트렐로에 필수 INSERT문 넣었습니다"));

				user.setRoles(Collections.singleton(userRole));

				
						
				People result = peopleRepository.save(user);

				URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{username}")
						.buildAndExpand(result.getId()).toUri();
				returnData.put("code", "1");
				returnData.put("message", "가입완료:)");
			} else {
				returnData.put("code", "2");
				returnData.put("message", "이미 가입 된 아이디입니다:(");
			}
		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "잠시 후, 다시 시도해주세요:(");
		}
		return returnData;
	}
}
