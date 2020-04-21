package us.flower.dayary.domain.DTO;

import java.util.Date;

import lombok.Data;
import us.flower.dayary.domain.BoardGroup;
import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.ToDoWriteList;


@Data
public class MoimBoardAllDTO {

    private long id;

    private People people;

    private BoardGroup boardGroup;

    private Moim moim;

    private String title;

    private String memo;

    private Date createDate;

    private Date updateDate;

    private char deleteFlag;

    private long viewCount;

    private long heart;

    private ToDoWriteList toDoWriteList;


}
