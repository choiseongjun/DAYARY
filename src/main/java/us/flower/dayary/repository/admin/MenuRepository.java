package us.flower.dayary.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import us.flower.dayary.domain.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long>{

}
