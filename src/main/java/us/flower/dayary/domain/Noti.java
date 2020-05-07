package us.flower.dayary.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//20191208 알림창 최성준
@Entity
@Table(name = "NOTI")
@Getter
@Setter
@ToString
public class Noti {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	@ManyToOne
	@JoinColumn(name = "PEOPLE_ID")
	People people;
	@ManyToOne
	@JoinColumn(name = "MOIM_ID")
	private Moim moim;

	@Column(name = "MEMO")
	private String memo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Column(name = "GUBUN_CD")
	private char gubunCd;
	
	
	
}
