package us.flower.dayary.service.moim.joinpeople;

import java.util.List;
import java.util.Map;

import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.People;

public interface MoimJoinPeopleService {

	void deleteMoimPeople(long moimPeopleNo);//모임탈퇴(자기자신)

	void banMoimpeople(People people, Moim moim);

	List<Long> searchJoinedPeople(long moimNo);

	Map<String, String> selectCountMoimPeople(long moimNo);

}
