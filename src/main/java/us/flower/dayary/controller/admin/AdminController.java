package us.flower.dayary.controller.admin;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import us.flower.dayary.domain.People;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("/admini")
	public String admini() {
		return "admin/adminmain";
	}
	@GetMapping("/adminPeople")
	public String adminPeople(HttpSession session) {
 		
		
		
		return "admin/adminPeopleTableList";
	}
}
