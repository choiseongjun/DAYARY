package us.flower.dayary.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.flower.dayary.domain.common.DateAudit;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Moim_Board_File")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoimBoardFile extends DateAudit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
	private long id;
    //모임 카테고리
    @ManyToOne(fetch = FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name = "MoimBoard_ID" ,referencedColumnName = "ID") 
    @JsonIgnore
	private MoimBoard moimBoard;
    @Column(name = "filename") 
	private String filename;


    @Column(name = "REAL_NAME")
	private String real_name;
    @Column(name = "FILE_SIZE")
	private String file_size;
    @Column(name = "FILE_LOCATE")
	private String file_locate;
    @Column(name="representImage")
    private long representImage;
}
