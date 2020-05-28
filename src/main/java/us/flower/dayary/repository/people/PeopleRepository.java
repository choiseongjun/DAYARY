package us.flower.dayary.repository.people;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import us.flower.dayary.config.social.connection.UserConnection;
import us.flower.dayary.domain.People;

public interface PeopleRepository extends JpaRepository<People, Long>{



	People findByEmail(String email);

	boolean existsByEmail(String email);
	@Query("select a.id as id from People a inner join MoimPeople b on a.id=b.people where b.joinrole='study' and a.id=(:people_no)")
	String findPeopleOne(@Param("people_no") long people_no);
	@Query("select b.id as id from People a inner join MoimPeople b on a.id=b.people inner join Moim c on b.moim=c.id where b.joinrole='study' and a.id=(:people_no) and c.id=(:no)")
	String findMoimPeopleNoOne(@Param("people_no") long peopleId,@Param("no") long no);

	boolean existsByImageName(String imageName);

	People findByName(String username);

	boolean existsByName(String name);

	People findBySocial(UserConnection userConnection);

    
 
}
