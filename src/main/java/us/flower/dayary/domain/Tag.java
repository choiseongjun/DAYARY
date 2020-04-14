package us.flower.dayary.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
//	    @ManyToMany(mappedBy = "tags")
//	    private Set<Moim> moims = new HashSet<>();
	    @OneToMany(fetch = FetchType.LAZY,orphanRemoval=true,mappedBy = "tag")
		@JsonIgnore 
		private List<MoimTag> tag;
	
}
