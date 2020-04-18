package us.flower.dayary.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import us.flower.dayary.domain.common.DateAudit;

@Entity
@Table(name = "Moim_Board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MoimBoard extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "PEOPLE_ID")
    private People people;

    @ManyToOne
    @JoinColumn(name = "BOARD_GROUP_ID")
    private BoardGroup boardGroup;

    @ManyToOne
    @JoinColumn(name = "MOIM_ID")
    private Moim moim;

    @Column(name = "TITLE")
    private String title;

    @Lob
    @Column(name = "MEMO")
    private String memo;

    @Column(name = "CREATE_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "UPDATE_DATE", updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(name="REPLY_COUNT")
    private long replyCount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "moimBoard", cascade = CascadeType.ALL)
    private List<MoimBoardReply> moimBoardReplies;

    @Column(name = "DELETE_FLAG")
    private char deleteFlag;

    @Column(name = "VIEW_COUNT")
    private long viewCount;

    @Column(name = "HEART")
    private long heart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "MOIM_TODO_WRITE_LIST_ID", nullable = true)
    private ToDoWriteList toDoWriteList;

    @OneToMany(orphanRemoval = true, mappedBy = "moimBoard", cascade = CascadeType.ALL)
    private List<MoimBoardFile> moimBoardfile = new ArrayList<MoimBoardFile>();

    @Getter
    private long moimNo;
}
