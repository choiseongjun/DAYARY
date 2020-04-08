package us.flower.dayary.payload;

import javax.validation.constraints.NotBlank;

import lombok.Data;
/**
 *  회원가입 파라미터 설정 2019-09월중순
 *   by 최성준
 */
@Data
public class SignUpRequest {
  

	@NotBlank
	private String email;
	@NotBlank
	private String password;

	private String name;

	private String photo;

	private String activation;
	
	private String interests;
	
	private String job;
	
	private String sex;
	
	private String introduce;
	
	
    
}