package us.flower.dayary.domain;

import lombok.Data;
import us.flower.dayary.domain.common.DateAudit;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 오프라인모임참가자
 *  * by choiseongjun
 */
@Entity
@Table(name="MEETUP_PEOPLE")
@Data
public class MeetupPeople extends DateAudit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private long id;
    @ManyToOne
	@JoinColumn(name = "MOIM_ID")
	@JsonIgnore
	private Moim moim;
	@ManyToOne
	@JoinColumn(name = "PEOPLE_ID")
	@JsonIgnore
	private People people;
	@ManyToOne
	@JoinColumn(name = "MEETUP_ID")
	@JsonIgnore
	private Meetup meetup;
	
	@Column(name="JOIN_CANCLE",nullable=false, columnDefinition = "CHAR(1) default 'N'")
	private char joinCancel;//모임 참여 취소 유무 체크하기위함..
}
