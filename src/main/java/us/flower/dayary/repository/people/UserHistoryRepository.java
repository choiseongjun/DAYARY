package us.flower.dayary.repository.people;

import org.springframework.data.jpa.repository.JpaRepository;

import us.flower.dayary.domain.UserHistory;

public interface UserHistoryRepository extends JpaRepository<UserHistory,Long>{

}
