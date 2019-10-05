package us.flower.dayary.domain;

import lombok.Data;
import us.flower.dayary.domain.common.DateAudit;
import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name="COMMUNITY_BOARD_REPLY")
public class CommunityBoardReply extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private long id;

//    @EmbeddedId
//    private CommunityBoardReplyId id;

    @Column(name="PEOPLE_ID")
    private long peopleId;

    @ManyToOne
    @JoinColumn(name = "COMMUNITY_BOARD_ID")
    private CommunityBoard communityBoard;

    @Column(name="BOARD_GROUP_ID")
    private  long boardGroupId;

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
    @ManyToOne
    private CommunityBoardReply parent;


    @Column(name="DELETE_FLAG" ,nullable=false, columnDefinition = "char(1) default 'N'")
    private char deleteFlag;
}
