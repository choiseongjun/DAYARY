package us.flower.dayary.domain;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.engine.FetchStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import us.flower.dayary.domain.common.DateAudit;

@Entity
@Table(name="MOIM_TODO_WRITE")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ToDoWrite extends DateAudit{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name="MOIM_ID")
	private Moim moim;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "PEOPLE_ID")
	private People people;
	
	@Column(name="PLAN_TITLE")
	private String plan_title;
	
	@Column(name="FROM_DATE")
	private Date from_date;
	
	@Column(name="TO_DATE")
	private Date to_date;
	
	@Column(name="To_DATE2")
	private Date to_date2;
	@Column(name="CREATE_DATE")
	private Date create_date;
//	@Column
//	private String count;
	@Column
	private double progress;
	@Column(name="PROGRESS_DONE")
	private long progress_done;
	@Column(name="PROGRESS_TOTAL")
	private long progress_total;
	
	@Column(name="STATUS")
	private String status;
//	@OneToMany(fetch = FetchType.LAZY,orphanRemoval=true,mappedBy = "toDoWrite")
//	@JsonIgnore
//	private List<ToDoWriteList> todowritelist;

	
	@Transient
	private long cnt;// 상태 그룹별 카운트

}
