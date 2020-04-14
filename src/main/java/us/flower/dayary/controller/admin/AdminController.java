package us.flower.dayary.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import us.flower.dayary.domain.People;
import us.flower.dayary.service.people.PeopleService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	PeopleService peopleService;
	
	@GetMapping("/admini")
	public String admini() {
		return "adminmain";
	}
	@GetMapping("/adminPeople")
	public String adminPeople(HttpSession session) {
 		
		
		return "admin/adminPeopleTableList";
	}
	@GetMapping("/adminMenu")
	public String adminMenu() {
		
		return "admin/adminMenu";
	}
	@GetMapping("/adminPeoplelist")
	@ResponseBody
	public List<People> adminPeoplelist(HttpSession session) {
 		
		List<People> peopleList = peopleService.selectAll();
		
		Map<String, Object> returnData = new HashMap<String, Object>();
		returnData.put("peopleList", peopleList);
		
		return peopleList;
	}
	
	
}
