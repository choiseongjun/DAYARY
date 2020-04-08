package us.flower.dayary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import us.flower.dayary.domain.common.DateAudit;

@Entity
@Table(name = "USER_HISTORY")
@Data
public class UserHistory extends DateAudit{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	@Column(name="LOGIN_DATE")
	private String logindate;
	@Column(name="OS_TYPE")
	private String ostype;
	@Column(name="ACCESS_NAME")
	private String accessname;
	@Column(name="ACCESS_PATH")
	private String accesspath;
	@Column(name="IPADDRESS")
	private String ipaddress;
	@Column(name="SESSION_LASTACCESS")
	private String sessionlastaccess;
	@ManyToOne
	@JoinColumn(name = "PEOPLE_ID")
	@JsonIgnore
	private People people;
}
