package us.flower.dayary.controller.community;


import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import us.flower.dayary.common.MediaUtils;
import us.flower.dayary.domain.CommunityBoard;
import us.flower.dayary.domain.CommunityBoardReply;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.UploadFile;
import us.flower.dayary.domain.DTO.BoardListDTO;
import us.flower.dayary.repository.community.BoardLikeRepository;
import us.flower.dayary.repository.community.CommunityBoardRepository;
import us.flower.dayary.repository.people.PeopleRepository;
import us.flower.dayary.security.CurrentUser;
import us.flower.dayary.service.community.CommunityBoardService;
import us.flower.dayary.service.community.image.CommunityImageService;



/**
 * 컨틀롤러 작명규칙
 * GET / POST / DELETE / PUT
 */

@Controller
public class CommunityBoardController {

	@Autowired
	CommunityBoardRepository communityBoardRepository;
	@Autowired
	CommunityBoardService communityBoardService;
	@Autowired
	PeopleRepository peopleRepository;
	@Autowired
	BoardLikeRepository boardLikeRepository;
	@Autowired
	CommunityImageService communityImageService;

	/**
	 * board group id 구하기
	 * @param boardGroup
	 * @return
	 */
	public Long getBoargdGroupId(String boardGroup){

		Long boardGroupId  = 0L;

		// check boardGroupId
		if(boardGroup.equals("free")){
			boardGroupId = 2L;
		}else if(boardGroup.equals("job")){
			boardGroupId = 3L;
		}else if(boardGroup.equals("certificate")){
			boardGroupId = 4L;
		}else if(boardGroup.equals("language")){
			boardGroupId = 5L;
		}else{
			// go to error
			boardGroupId= 1L;
		}

		return boardGroupId;
	}


	/**
	 *  게시판 리스트 조회
	 * 2019-09-30 minholee
	 * @param boardGroup
	 * @param model
	 * @param pageable
	 * @param session
	 * @return
	 */
	@GetMapping("/community/board/{boardGroup}")
	public String getBoardList(@PathVariable("boardGroup") String boardGroup, Model model,
							@PageableDefault(sort = "id", direction = Sort.Direction.DESC, size=25) Pageable pageable, @RequestParam(name = "search", defaultValue = "") String search, HttpSession session) {

		// get board group id
		Long boardGroupId  = getBoargdGroupId(boardGroup);

		// board group
		model.addAttribute("boardGroup",boardGroup);

		// session check
		session.setAttribute("page", pageable.getPageNumber());

		// service
		Page<BoardListDTO> communityBoardList = communityBoardService.getCommunityBoardList(boardGroupId, pageable, search);

		// contents list
		model.addAttribute("boardList", communityBoardList.getContent());

		// total element count
		model.addAttribute("boardListCount", communityBoardList.getTotalElements());

		// page number
		model.addAttribute("pageNumber", communityBoardList.getTotalPages());

		// search text
		if(search.equals("")){
			model.addAttribute("search", 0);
		}else{
			model.addAttribute("search", search);
		}


		return "community/boardList";
	}

	/**
	 *  타임라인 조회 (전체 게시글 조회)
	 * @param model
	 * @return
	 */
	@GetMapping("/community/timeLine")
	public String getTimeLineList(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();





		System.out.println(auth);
		System.out.println("@#$#@$@#$@#@#");
		// service
		List<CommunityBoard> timeLineList = communityBoardService.getCommunityBoardList(1L);
	
		
		// contents list
		model.addAttribute("timeLineList",timeLineList);
		model.addAttribute("menu3_hover","on");
		return "community/timeLineList";
	}

	/**
	 * 타임라인 조회 (본인글 조회)
	 *
	 * @param
	 * @return
	 * @throws
	 * @author choiseongjun
	 */
	@GetMapping("/community/timeLine/my")
	public String getTimeLineListSelf(Model model,HttpSession session) {

		// session check
		long peopleId = (long) session.getAttribute("peopleId");

		// service
		List<CommunityBoard> timeLineList = communityBoardService.getCommunityBoardList(1L, peopleId);

		// contents list
		model.addAttribute("timeLineList",timeLineList);

		return "community/timeLineList";
	}

	/**
	 *  게시판 게시글 삭제
	 * @param boardId
	 * @param session
	 * @return
	 */
	@ResponseBody
	@DeleteMapping("/community/board/delete/{boardId}")
	public Map<String, Object> deleteBoard(@PathVariable("boardId") long boardId,
							  HttpSession session){

		// return message
		Map<String, Object> returnData = new HashMap<>();

		// session user id
		Long peopleId = (Long) session.getAttribute("peopleId");

		// 게시글 작성자와 현재 세션의 사용자 같은지
		boolean check = communityBoardService.checkWriter(peopleId, boardId);

		// 게시글 작성자가 본인이라면
		if(check){
			try {
				// 게시글 Delete Flag를 'Y'로 변경
				communityBoardService.deleteBoard(boardId);
				returnData.put("code", "1");
				returnData.put("message", "삭제되었습니다");

			} catch (Exception e) {
				returnData.put("code", "E3290");
				returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
			}
		}else {
				returnData.put("code", "E3290");
				returnData.put("message", "게시글 작성자만 삭제할 수 있습니다.");
		}

		return returnData;
	}

	/**
	 *  게시글 추천
	 * @param boardId
	 * @param boardGroup
	 * @param session
	 * @return
	 */
	@ResponseBody
	@PostMapping("/community/board/{boardGroup}/like/{boardId}")
	public Map<String, Object> addLike( @PathVariable("boardGroup") String boardGroup, @PathVariable("boardId") long boardId,
							HttpSession session) {

		// return value
		Map<String, Object> returnData = new HashMap<>();

		// people id
		Long peopleId = (Long) session.getAttribute("peopleId");

		// boardGroupId
		Long boardGroupId = getBoargdGroupId(boardGroup);

		// 이전 추천했는지 확인 (TRUE 이면 기존 추천글)
		boolean boardLike = communityBoardService.checkBoardLike(peopleId, boardId, boardGroupId);

		// 추천하지 않았던 게시글이면
		if(!boardLike){
			// 추천이력 등록
			communityBoardService.addBoardLike(peopleId, boardId, boardGroupId);
			returnData.put("code", "1");
		}else{
			returnData.put("code", "2");
			returnData.put("message", "이미 추천한 게시글 입니다");
		}

		return returnData;
	}

	/**
	 *  게시글 추천 해제
	 * @param boardId
	 * @param boardGroup
	 * @param session
	 * @return
	 */
	@ResponseBody
	@PostMapping("/community/board/{boardGroup}/unLike/{boardId}")
	public Map<String, Object> removeLike( @PathVariable("boardGroup") String boardGroup, @PathVariable("boardId") long boardId,
										  HttpSession session) {

		// return value
		Map<String, Object> returnData = new HashMap<>();

		// people id
		Long peopleId = (Long) session.getAttribute("peopleId");

		// boardGroupId
		Long boardGroupId = getBoargdGroupId(boardGroup);

		// 이전 추천했는지 확인 (TRUE 이면 기존 추천글)
		boolean boardLike = communityBoardService.checkBoardLike(peopleId, boardId, boardGroupId);

		// 추천했던 게시글이면
		if(boardLike){
			// 추천이력 제거
			communityBoardService.removeBoardLike(peopleId, boardId, boardGroupId);
			returnData.put("code", "1");
		}else{
			returnData.put("code", "2");
			returnData.put("message", "추천하지않은 게시글 입니다");
		}

		return returnData;
	}

	/**
	 * 게시글 Detail
	 * 2019-09-30 minholee
	 * @param boardId
	 * @param session
	 * @param model
	 * @return
	 */
	@GetMapping("/community/board/{boardGroup}/detail/{boardId}")
	public String getBoardDetail(@PageableDefault Pageable pageable,@PathVariable("boardGroup") String boardGroup, @PathVariable("boardId") long boardId,
							  HttpSession session, Model model) {
		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
		pageable = PageRequest.of(page, 9, Sort.Direction.DESC, "id");// 내림차순으로 정렬한다
		// board group (게시판 그룹)
		model.addAttribute("boardGroup",boardGroup);

		// communityBoard (게시글)
		CommunityBoard communityBoard = communityBoardService.getCommunityBoard(boardId);
		model.addAttribute("board", communityBoard);

		// 조회수 업데이트
		communityBoardService.addViewCount(communityBoard);

		// 게시글 작성자와 현재 세션의 사용자 같은지
		Long peopleId = (Long) session.getAttribute("peopleId");
		boolean writer = communityBoardService.checkWriter(peopleId, communityBoard);

		// 게시글 작성자가 본인이라면
		if(writer){
			model.addAttribute("writerFlag", TRUE);
		}else{
			model.addAttribute("writerFlag", FALSE);
		}

		// TRUE 면 기존에 추천한 게시글
		boolean boardLike = communityBoardService.checkBoardLike(peopleId, boardId, getBoargdGroupId(boardGroup));
		if(boardLike){
			model.addAttribute("boardLike", TRUE);
		}else{
			model.addAttribute("boardLike", FALSE);
		}

		// set session page number (이전페이지 돌아갈때 사용)
		model.addAttribute("page", session.getAttribute("page"));

		// 댓글 (전체)
		model.addAttribute("replies", communityBoardService.getCommunityReplyList(boardId,pageable));

		// 사용자 id (댓글 삭제용)
		model.addAttribute("id", peopleId);

		return "community/boardDetail";
	}



	/**
	 *  게시글 쓰기 (GET)
	 * @param boardGroup
	 * @param session
	 * @param model
	 * @return
	 */
	@GetMapping("/community/board/{boardGroup}/write")
	public String studyWrite(@PathVariable("boardGroup") String boardGroup, HttpSession session, Model model) {

		// board group
		model.addAttribute("boardGroup",boardGroup);

		// people name
		long peopleId = (Long) session.getAttribute("peopleId");
		People people = peopleRepository.getOne(peopleId);
		model.addAttribute("name", people.getName());

		// set session page number
        model.addAttribute("page", session.getAttribute("page"));

		return "community/boardWrite";
	}


	/**
	 * 이미지 저장 (2019-10-23)
	 * @param file
	 * @return
	 */
	@ResponseBody
	@PostMapping("/community/board/image")
	public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
		try{
			UploadFile uploadFile = communityImageService.store(file);
			return ResponseEntity.ok().body("/community/board/image/" + uploadFile.getId());
		}catch(Exception e){
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}


	@GetMapping("/community/board/image/{fileId}")
	@ResponseBody
	public ResponseEntity<?> serveFile(@PathVariable long fileId) {
		try {
			UploadFile uploadedFile = communityImageService.load(fileId);
			HttpHeaders headers = new HttpHeaders();

			Resource resource = communityImageService.loadAsResource(uploadedFile.getSaveFileName());
			String fileName = uploadedFile.getFileName();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

			if (MediaUtils.containsImageMediaType(uploadedFile.getContentType())) {
				headers.setContentType(MediaType.valueOf(uploadedFile.getContentType()));
			} else {
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			}

			return ResponseEntity.ok().headers(headers).body(resource);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	/**
     * 게시글 글쓰기 (POST)
     *
     * @param
     * @return
     * @throws
     * @author minholee
     */
	@ResponseBody
	@PostMapping("/community/board/{boardGroup}/write")
	public Map<String, Object> studyWrite(@PathVariable("boardGroup") String boardGroup, @RequestBody CommunityBoard communityBoard,
							 HttpSession session) {

		Map<String, Object> returnData = new HashMap<String, Object>();

		if (communityBoard.getTitle().equals(null) || communityBoard.getTitle().equals("")) {
			returnData.put("code", "0");
			returnData.put("message", "제목을 입력해주세요");
			return returnData;
		}

		if (communityBoard.getMemo().equals(null) || communityBoard.getMemo().equals("")) {
			returnData.put("code", "0");
			returnData.put("message", "내용을 입력해주세요");
			return returnData;
		}

		Long peopleId = (Long) session.getAttribute("peopleId");//사용자세션정보 들고오기
		Long boardGroupId = getBoargdGroupId(boardGroup);

		try {
			communityBoardService.communityWrite(peopleId,boardGroupId,communityBoard);
			returnData.put("code", "1");
			returnData.put("message", "저장되었습니다");

		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}

		return returnData;
	}

	/**
	 * 타임라인 글쓰기
	 *
	 * @param
	 * @return
	 * @throws
	 * @author minholee
	 */
	@ResponseBody
	@PostMapping("/community/timeLine/write")
	public Map<String, Object> studyWrite(@RequestBody CommunityBoard communityBoard,
										  HttpSession session) {

		Map<String, Object> returnData = new HashMap<String, Object>();


		if (communityBoard.getMemo().equals(null) || communityBoard.getMemo().equals("")) {
			returnData.put("code", "0");
			returnData.put("message", "내용을 입력해주세요");
			return returnData;
		}

		Long peopleId = (Long) session.getAttribute("peopleId");//사용자세션정보 들고오기
		Long boardGroupId = 1L;

		try {
			communityBoardService.communityWrite(peopleId,boardGroupId,communityBoard);
			returnData.put("code", "1");
			returnData.put("message", "저장되었습니다");

		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}

		return returnData;
	}

	/**
	 * 커뮤니티리스트 글 수정
	 *
	 * @param
	 * @return
	 * @throws
	 * @author minholee
	 */
	@ResponseBody
	@PostMapping("/community/board/{boardGroup}/modify/{boardId}")
	public Map<String, Object> studyModify(@PathVariable("boardGroup") String boardGroup,
										   @PathVariable("boardId") long boardId, @RequestBody CommunityBoard communityBoard,
										  HttpSession session) {

		Map<String, Object> returnData = new HashMap<>();

		if (communityBoard.getTitle().equals(null) || communityBoard.getTitle().equals("")) {
			returnData.put("code", "0");
			returnData.put("message", "제목을 입력해주세요");
			return returnData;
		}

		if (communityBoard.getMemo().equals(null) || communityBoard.getMemo().equals("")) {
			returnData.put("code", "0");
			returnData.put("message", "내용을 입력해주세요");
			return returnData;
		}

		// 게시글 작성자와 현재 세션의 사용자 같은지
		Long peopleId = (Long) session.getAttribute("peopleId");
		boolean check = communityBoardService.checkWriter(peopleId, boardId);

		if(check){
			try {
				// modify board
				CommunityBoard modifyBoard = communityBoardRepository.getOne(boardId);
				modifyBoard.setTitle(communityBoard.getTitle());
				modifyBoard.setMemo(communityBoard.getMemo());
				communityBoardRepository.save(modifyBoard);

				returnData.put("code", "1");
				returnData.put("message", "수정되었습니다");

			} catch (Exception e) {
				returnData.put("code", "E3290");
				returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
			}
		}else{
			returnData.put("code", "E3290");
			returnData.put("message", "게시글 작성자만 수정할 수 있습니다.");
		}


		return returnData;
	}


	/**
	 *  댓글 삭제
	 * @param replyId
	 * @param session
	 * @return
	 */
	@ResponseBody
	@DeleteMapping("/community/board/reply/delete/{replyId}")
	public Map<String, Object> deleteReply(@PathVariable("replyId") long replyId,
										   HttpSession session){

		// return message
		Map<String, Object> returnData = new HashMap<>();

		// session user id
		Long peopleId = (Long) session.getAttribute("peopleId");

		// 댓글 작성자와 현재 세션의 사용자 같은지
		boolean check = communityBoardService.checkReplyWriter(peopleId, replyId);

		// 댓글 작성자가 본인이라면
		if(check){
			try {
				returnData.put("code", "1");
				returnData.put("message", "삭제되었습니다");

			} catch (Exception e) {
				returnData.put("code", "E3290");
				returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
			}
		}else {
			returnData.put("code", "E3290");
			returnData.put("message", "게시글 작성자만 삭제할 수 있습니다.");
		}

		return returnData;
	}

	/**
	 * 게시글 댓글저장
	 * @param boardGroup
	 * @param boardId
	 * @param session
	 * @param model
	 * @return
	 */
	@ResponseBody
	@PostMapping("/community/board/{boardGroup}/reply/{boardId}")
	public HashMap<String, Object> postBoardReply(@PathVariable("boardGroup") String boardGroup, @PathVariable("boardId") long boardId,
												  @RequestBody CommunityBoardReply reply, HttpSession session, Model model){
		// RequestBody : CommunityBoard / reply ??

		HashMap<String, Object> returnData = new HashMap<>();

		if (reply.getMemo().equals(null) || reply.getMemo().equals("")) {
			returnData.put("code", "0");
			returnData.put("message", "내용을 입력해주세요");
			return returnData;
		}

		Long peopleId = (Long) session.getAttribute("peopleId");//사용자세션정보 들고오기
		Long boardGroupId = getBoargdGroupId(boardGroup);
		
		try {
			reply = communityBoardService.addBoardReply(reply, peopleId, boardId, boardGroupId);
			returnData.put("code", "1");
			returnData.put("message", "저장되었습니다");
			// 저장된 댓글 번호
			returnData.put("id", reply.getId());
			returnData.put("memo", reply.getMemo());

		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}

		return returnData;
	}
	/**
	 * 게시글 댓글수정
	 * @param boardGroup
	 * @param boardId
	 * @param session
	 * @param model
	 * @return
	 */
	@ResponseBody
	@PostMapping("/community/board/modifyReply")
	public HashMap<String, Object> modifyBoardReply( @RequestBody CommunityBoardReply reply, HttpSession session, Model model){
		

		HashMap<String, Object> returnData = new HashMap<>();

		if (reply.getMemo().equals(null) || reply.getMemo().equals("")) {
			returnData.put("code", "0");
			returnData.put("message", "내용을 입력해주세요");
			return returnData;
		}

		
		try {
			communityBoardService.moidfyBoardReply(reply);
			returnData.put("code", "1");
			returnData.put("message", "저장되었습니다");
		

		} catch (Exception e) {
			returnData.put("code", "E3290");
			returnData.put("message", "데이터 확인 후 다시 시도해주세요.");
		}

		return returnData;
	}
	/**
	 * 타임라인 댓글리스트
	 * @param boardId
	 * @param session
	 * @param model
	 * @return
	 */
	
	@ResponseBody
	@PostMapping("/community/timeLine/replyList/{boardId}")
	public Map<String, Object> timeLineReplyList(@PathVariable("boardId") long boardId,
			@PageableDefault Pageable pageable, HttpSession session) {
		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
		pageable = PageRequest.of(page, 9, Sort.Direction.DESC, "id");// 내림차순으로 정렬한다
		
		Map<String, Object> returnData = new HashMap<String, Object>();
		returnData.put("list", communityBoardService.getTimeLineReplyList(boardId,pageable));
	
		return returnData;
	}

}
