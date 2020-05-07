package us.flower.dayary.domain;

import lombok.Data;

import java.sql.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 오프라인모임
 *   by choiseongjun
 */
@Entity
@Table(name="MEETUP")
@Data
public class Meetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private long id;
	@ManyToOne
	@JoinColumn(name="MOIM_ID")
	private Moim moim;
	@ManyToOne
	@JoinColumn(name = "PEOPLE_ID")
	private People people;
	@Column(name = "TITLE")
	private String title;
	@Lob //길이 제한 없음
	@Column(name = "INTRO")
	private String intro;
    @JsonFormat(pattern="mm/DD/yyyy")
	@Column(name="MEET_DATE")
    private Date meetDate;
    @Lob //길이 제한 없음
    @Column(name="DETAIL_ADDRESS")
    private String detailAddress;
    @Column(name="LOCATION_X")
    private double locationX;
    @Column(name="LOCATION_Y")
    private double locationY;
    @Column(name="MONEY")
    private long money;
    @Column(name="PEOPLE_LIMIT")
    private long peopleLimit;
    @Column(name="CREATE_DATE")
    private Date createDate;
    
    
}
