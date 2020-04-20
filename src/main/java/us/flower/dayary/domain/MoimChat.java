package us.flower.dayary.domain;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="MOIMCHAT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoimChat {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "MOIM_ID")
	@JsonIgnore
	private Moim moim;
	@ManyToOne
	@JoinColumn(name = "PEOPLE_ID")
	@JsonIgnore
	private People people;
	@Lob
	@Column(name="CHAT_MEMO")
	private String chatMemo;
 	@Column(name = "CREATE_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP) 
    private Date createDate;
 	
 	
}
