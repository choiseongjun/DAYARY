package us.flower.dayary.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import us.flower.dayary.domain.common.DateAudit;

/**
 * 파일 객체 (MOIM_FILE, COMMUNIY_FILE)
 */
@Entity
@Data
@Table(name = "FILE")
public class File extends DateAudit {

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