package us.flower.dayary;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.Tag;
import us.flower.dayary.domain.ToDoWrite;
import us.flower.dayary.domain.VisitCheck;
import us.flower.dayary.repository.moim.MoimRepository;
import us.flower.dayary.repository.visitcheck.VisitCheckRepository;
import us.flower.dayary.service.moim.moimService;

@Controller
public class HomeController {
	@Autowired
	VisitCheckRepository visitcheckRepository;
	@Autowired
	MoimRepository moimRepository;
	@Autowired
	moimService moimService;
	
	@RequestMapping("/")
	public String Home(@PageableDefault Pageable pageable,Model model,Principal principal) throws Exception {
		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
		pageable = PageRequest.of(page, 2, Sort.Direction.DESC, "id");// 내림차순으로 정렬한다
//		Optional<Moim> moim = moimRepository.findById((long) 48);
//		return moim.get();

		
		VisitCheck vo=new VisitCheck();
		insertVisitor(vo);
		Page<Moim> moimList = moimService.selectMoimAll(pageable);// 모든 모임리스트 출력한다
		
		for(int i=0;i<moimList.getNumberOfElements();i++) {
			
			List<ToDoWrite> todowrite =  moimList.getContent().get(i).getTodowrite();
			double progresstotalSum=0;
			double progresstotal=0;
			double progressbefore = 0;
			double progress = 0;
			long count = 0;
			
			String hashtag="";
			for(int j=0;j<moimList.getContent().get(i).getMoimtag().size();j++) {
			Tag tags =moimList.getContent().get(i).getMoimtag().get(j).getTag();
				hashtag +="#"+tags.getName();
			}
			for(ToDoWrite j: todowrite) {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("progress_done", j.getProgress_done());
				tempMap.put("sametodoid",j.getMoim().getId());
				progresstotalSum+=j.getProgress_done();
				progresstotal+=j.getProgress_total();
				progressbefore += j.getProgress();
				count++;
			}
			if(count==0) {
			count = 0;
			}
			
				double progressPercent=0;
				//progress = Double.parseDouble(String.format("%.2f",progressbefore / count));
				progressPercent = Math.round(((progresstotalSum/progresstotal)*100)*100)/100.0;
				moimList.getContent().get(i).setProgresssum((long)progresstotalSum);
				moimList.getContent().get(i).setProgresstotal((long)progresstotal);
				moimList.getContent().get(i).setProgresspercent(progressPercent);
				moimList.getContent().get(i).setTodocount(count);
				moimList.getContent().get(i).setHashtag(hashtag);
		}
		model.addAttribute("moimList", moimList);
		model.addAttribute("principal",principal);
		return "main";
	}
	//방문자체크 메서드
	public VisitCheck insertVisitor(VisitCheck vo) throws Exception{
        HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        vo.setVisit_ip(req.getRemoteAddr());
        vo.setVisit_agent(req.getHeader("User-Agent"));//브라우저 정보
        vo.setVisit_refer(req.getHeader("referer"));//접속 전 사이트 정보
        return visitcheckRepository.save(vo);
    }
	@GetMapping("/access-denied")
    public String accessDenied() {
		System.out.println("access denied");
        return "/error/access-denied";
    }
	
	

}
