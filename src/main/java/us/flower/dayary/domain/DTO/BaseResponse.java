package us.flower.dayary.domain.DTO;

import lombok.Data;

@Data
public class BaseResponse {

	
	private String message;
	private String code;

	public BaseResponse() {
		this.message = "성공";
		this.code ="1";
	}
	
	public BaseResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}



	
	
}
