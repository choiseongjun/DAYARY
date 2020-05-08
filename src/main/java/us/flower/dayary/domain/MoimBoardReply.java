package us.flower.dayary.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import us.flower.dayary.domain.common.DateAudit;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "MOIM_BOARD_REPLY")
@ToString(exclude = "moimBoard, parent,people")
@DynamicInsert
@DynamicUpdate
public class MoimBoardReply extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = People.class)
    @JoinColumn(name = "PEOPLE_ID", nullable = false)
    private People people;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MOIM_BOARD_ID")
    private MoimBoard moimBoard;

    @Column(name = "BOARD_GROUP_ID")
    private long boardGroupId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<MoimBoardReply> child;

    @Column(name = "MEMO", nullable = false)
    @Lob
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    private MoimBoardReply parent;


    @Column(name = "DELETE_FLAG", nullable = false)
    @ColumnDefault("'N'")
    private String deleteFlag;
}
