package us.flower.dayary.repository.chat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import us.flower.dayary.domain.MoimChat;

public interface MoimChatRepository extends JpaRepository<MoimChat, Long>{

	List<MoimChat> findByMoim_idAndCreateDateBetween(long no,Timestamp date,Timestamp date2);

	long countByMoim_idAndCreateDateBetween(long no,Timestamp date,Timestamp date2);

}
