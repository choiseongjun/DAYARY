package us.flower.dayary.service.moim.board;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import us.flower.dayary.domain.CommunityBoard;
import us.flower.dayary.domain.CommunityBoardReply;
import us.flower.dayary.domain.DTO.BoardReplyDTO;
import us.flower.dayary.domain.DTO.MoimBoardReplyDTO;
import us.flower.dayary.domain.MoimBoard;
import us.flower.dayary.domain.DTO.MoimBoardListDTO;
import us.flower.dayary.domain.MoimBoardReply;

public interface MoimBoardService {

	// 모임 게시판 아이디로 조회
	public MoimBoard getMoimBoard(long boardId);
	
	public Page<MoimBoard> getMoimBoardByMoimIdPaging(Pageable pageable, long moimId);
	public List<MoimBoard> getMoimBoardByMoimId(long moimId);

	//모임 게시판 글 조회
	Page<MoimBoardListDTO> getMoimBoardList(Long moimId, Long boardGroupId, Pageable pageable, String search);

	// 조회수 + 1
	void addViewCount(MoimBoard moimBoard);

	// 게시글 작성자와 사용자 아이디 같은지 확인
	boolean checkWriter(Long peopleId, long moimBoardId);

	// 게시글 작성자와 사용자 아이디 같은지 확인
	boolean checkWriter(Long peopleId, MoimBoard moimBoard);

	// 이미 추천한 게시글인지 확인
	boolean checkBoardLike(long peopleId, long boardId, long boardGroupId);

	// 게시글 댓글 작성자와 사용자 아이디 같은지 확인
	boolean checkReplyWriter(Long peopleId, long replyId);

	// 게시글 추천
	void addBoardLike(long people, long boardId, long BoardGroupId);

	// 게시글 추천 삭제
	void removeBoardLike(long peopleId, long boardId, long BoardGroupId);

	//게시글 쓰기
	void moimBoardWrite(Long no, Long peopleId, Long boardGroupId, MoimBoard moimBoard);

	void updateBoard(long boardId, MoimBoard moimBoard);

	// 게시글 삭제
	void deleteBoard(long boardId);

	// 게시글 댓글 저장
	MoimBoardReply addBoardReply(MoimBoardReply reply, long peopleId, long boardId, long boardGroupId);

	//게시글 댓글 수정
	void moidfyBoardReply(MoimBoardReply reply);

	// 댓글 삭제
	void deleteReply(MoimBoardReply reply);

	// 댓글 리스트 (대댓글은 DeleteFlag가 "N"인 경우만)
	List<MoimBoardReplyDTO> getMoimReplyList(long boardId, Pageable pageable);

	////////////////////////////////////////////////////////////////////////////////////

//	// 게시글 작성자와 사용자 아이디 같은지 확인
//	boolean checkWriter(Long peopleId, long moimBoardId);
//
//	// 게시글 작성자와 사용자 아이디 같은지 확인
//	boolean checkWriter(Long peopleId, CommunityBoard moimBoard);
//
//	// 게시글 Detail
//	MoimBoard getMoimBoard(long boardId);

}
