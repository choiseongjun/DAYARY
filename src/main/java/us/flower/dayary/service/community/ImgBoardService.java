package us.flower.dayary.service.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ModelAttribute;
import us.flower.dayary.domain.BoardGroup;
import us.flower.dayary.domain.CommunityBoard;

import java.util.List;

public interface ImgBoardService {
	void saveImgBoard(@ModelAttribute CommunityBoard communityBoard);
}
