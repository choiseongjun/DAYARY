package us.flower.dayary.controller.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import us.flower.dayary.domain.BoardGroup;
import us.flower.dayary.domain.CommunityBoard;
import us.flower.dayary.repository.community.CommunityBoardRepository;


@Controller
public class ImgBoardController {

	@Autowired CommunityBoardRepository communityBoardRepository;

	 /**
     * 커뮤니티 StudyCafeList 조회
     *
     * @param
     * @return
     * @throws
     * @author choiseongjun
     */
	@GetMapping("/community/studycafeList/{board_group_no}")
	public String studcafeList(@PathVariable("board_group_no") long board_group_no) {

		return "community/studycafeList";
	}

	/**
	 * 커뮤니티 studycafeWrite 글쓰기
	 *
	 * @param
	 * @return
	 * @throws
	 * @author choiseongjun
	 */
	@GetMapping("/community/studycafeWrite/{board_group_no}")
	public String studycafeWrite(@PathVariable("board_group_no") long board_group_no) {

		return "community/studycafeWrite";
	}

	/**
	 * 커뮤니티 studycafeWrite 글쓰기
	 *
	 * @param
	 * @return
	 * @throws
	 * @author choiseongjun
	 */
	@PostMapping("/community/studycafeWriting/{board_group_no}")
	public String studycafeWriting(@ModelAttribute CommunityBoard communityBoard, @PathVariable("board_group_no") long board_group_no) {

		return "community/studycafeList";
	}

	 /**
     * 커뮤니티 StudyCafeDetail 조회
     *
     * @param
     * @return
     * @throws
     * @author choiseongjun
     */
	@GetMapping("/community/studycafeDetail")
	public String studycafeDetail() {

		return "community/studycafeDetail";
	}
}
