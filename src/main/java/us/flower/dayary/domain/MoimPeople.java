package us.flower.dayary.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import us.flower.dayary.domain.common.DateAudit;

/**
 * 온라인모임참가자
 * by choiseongjun
 */
@Entity
@Table(name = "MOIM_PEOPLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name="MOIM_PEOPLE_GEN", //시퀀스 제너레이터 이름
        sequenceName="MOIM_PEOPLE_SEQ", //시퀀스 이름
        initialValue=1, //시작값
        allocationSize=1 //메모리를 통해 할당할 범위 사이즈
        )
public class MoimPeople extends DateAudit{

	
//	@GeneratedValue(
//            strategy=GenerationType.SEQUENCE, //사용할 전략을 시퀀스로  선택
//            generator="MOIM_PEOPLE_GEN" //식별자 생성기를 설정해놓은  USER_SEQ_GEN으로 설정        
//            )
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "MOIM_ID")
	@JsonIgnore
	private Moim moim;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PEOPLE_ID")
	@JsonIgnore
	private People people;
	 
	@Column(name="JOINROLE")
	private String joinrole;
	// Y은 만든사람 N은 참여자
	@Column(name="MAKER" ,nullable=false, columnDefinition = "char(1) default 'N'")
	private char maker;

	//참가자 Y은 승인된 사람 N은 비승인자
	@Column(name="JOIN_CONDITION" ,nullable=false, columnDefinition = "char(1) default 'Y'")
	private char joinCondition;

}
