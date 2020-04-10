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

@Entity
@Table(name = "MOIM_TAG")
@Data
public class MoimTag{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private long id;
	@ManyToOne
	@JoinColumn(name = "MOIM_ID")
	@JsonIgnore
	private Moim moim;

	@ManyToOne
	@JoinColumn(name = "TAG_ID")
	@JsonIgnore
	private Tag tag;
}
