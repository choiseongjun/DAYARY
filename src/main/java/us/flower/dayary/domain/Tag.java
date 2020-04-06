package us.flower.dayary.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;
import us.flower.dayary.domain.common.DateAudit;

@Entity
@Table(name = "TAG")
@Data
public class Tag extends DateAudit{
	   	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "ID")
	    private long id;
	   	
	    @Column(name = "NAME")
	    private String name;
	    
	    @ManyToMany(fetch = FetchType.LAZY)
	    @JoinTable(
	    name = "MOIM_TAG",                              // 연결테이블 이름
	    joinColumns = @JoinColumn(name = "TAG_ID"),        // 태그와 매핑할 조인 컬럼 정보를 지정
	    inverseJoinColumns = @JoinColumn(name = "MOIM_ID") // 모임와 매핑할 조인 컬럼 정보를 지정
	    )
	    private List<Moim> moims = new ArrayList<>();
}
