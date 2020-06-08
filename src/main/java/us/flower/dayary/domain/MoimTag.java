package us.flower.dayary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "MOIM_TAG")
@Data
public class MoimTag{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MOIM_ID")
	@JsonIgnore
	@OnDelete(action= OnDeleteAction.NO_ACTION)
	private Moim moim;

	@ManyToOne
	@JoinColumn(name = "TAG_ID")
	@JsonIgnore
	private Tag tag;
}
