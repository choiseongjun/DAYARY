package us.flower.dayary.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.ToString;
import us.flower.dayary.domain.common.DateAudit;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name="COMMUNITY_BOARD")
@Data
@ToString(exclude = "communityBoardReplies")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class CommunityBoard extends DateAudit{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = People.class)
	@JsonIgnore
	@JoinColumn(name = "PEOPLE_ID", nullable = false)
	private People people;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = BoardGroup.class)
	@JsonIgnore
	@JoinColumn(name = "BOARD_GROUP_ID", nullable = false)
	private BoardGroup boardGroup;

	@Column(name="TITLE")
	private String title;

	@Column(name="MEMO", nullable = false)
	@Lob
	private String memo;

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "communityBoard")
//	private List<File> files;

	@Column(name="REPLY_COUNT")
	private long replyCount;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "communityBoard", cascade = CascadeType.ALL)
	private List<CommunityBoardReply> communityBoardReplies;

	@Column(name="DELETE_FLAG" ,nullable=false)
	private String deleteFlag;

	@Column(name="VIEW_COUNT")
	private long viewCount;

	@Column(name="HEART")
	private long heart;
	
	
}
