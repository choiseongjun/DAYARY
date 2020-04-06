package us.flower.dayary.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import us.flower.dayary.domain.Menu;
import us.flower.dayary.service.admin.MenuService;

@Controller
public class MenuController {

	@Autowired
	MenuService menuService;
	
	@GetMapping("/Menulist")
	@ResponseBody
	public List<Menu> getAllMenu(){
		List<Menu> menuList=menuService.findMenuList();
		return menuList;
	}

}
