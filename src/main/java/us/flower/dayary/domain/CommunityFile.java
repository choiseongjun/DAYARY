package us.flower.dayary.domain;

import lombok.Data;
import us.flower.dayary.domain.common.DateAudit;

import javax.persistence.*;

@Entity
@Data
@Table(name="COMMUNITY_FILE")
public class CommunityFile extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name="COMMUNITY_BOARD_ID")
    private CommunityBoard communityBoard;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name="REAL_NAME")
    private String realName;

    @Column(name="FILE_SIZE")
    private long fileSize;

    @Column(name="FILE_LOCATE")
    private String fileLocate;
}
