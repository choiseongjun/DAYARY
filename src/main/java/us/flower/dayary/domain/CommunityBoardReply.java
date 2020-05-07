package us.flower.dayary.domain;


import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import us.flower.dayary.domain.common.DateAudit;
import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name="COMMUNITY_BOARD_REPLY")
@ToString(exclude = "communityBoard, parent,people")
@DynamicInsert
@DynamicUpdate
public class CommunityBoardReply extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private long id;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = People.class)
	@JoinColumn(name = "PEOPLE_ID", nullable = false)
    private People people;

    @ManyToOne(optional = false)
    @JoinColumn(name = "COMMUNITY_BOARD_ID")
    private CommunityBoard communityBoard;

    @Column(name="BOARD_GROUP_ID")
    private long boardGroupId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<CommunityBoardReply> child;

    @Column(name="MEMO", nullable = false)
    @Lob
    private String memo;

//    @JoinColumns({
//            @JoinColumn(name="PEOPLE_ID", referencedColumnName = "PEOPLE_ID", insertable = false, updatable = false),
//            @JoinColumn(name="COMMUNITY_BOARD_ID", referencedColumnName = "COMMUNITY_BOARD_ID", insertable = false, updatable = false),
//            @JoinColumn(name="BOARD_GROUP_ID", referencedColumnName = "BOARD_GROUP_ID", insertable = false, updatable = false),
//    })
    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityBoardReply parent;


    @Column(name="DELETE_FLAG" ,nullable=false)
    @ColumnDefault("'N'")
    private String deleteFlag;
}
