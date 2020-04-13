package us.flower.dayary.domain.DTO;


import lombok.Data;

@Data
public class Message {

	private long peopleId;
	private long moimNo;
	private String msg;
	private String peopleEmail;
	private String createDate;
	
}
