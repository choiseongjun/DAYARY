package us.flower.dayary.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.flower.dayary.domain.Menu;
import us.flower.dayary.repository.admin.MenuRepository;

@Service
public class MenuServiceImpl implements MenuService{

	@Autowired
	MenuRepository menuRepository;
	
	@Override
	public List<Menu> findMenuList() {
		return menuRepository.findAll();
	}

}
