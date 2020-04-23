package us.flower.dayary.domain.DTO;


import javax.persistence.Column;
import javax.persistence.Transient;

import lombok.Data;

@Data
public class Message {

	private long peopleId;
	private long moimNo;
	private String msg;
	private String peopleEmail;
	private String createDate;
    private String imagePath;
    private String imageName;
    private String imageExtension;
}
