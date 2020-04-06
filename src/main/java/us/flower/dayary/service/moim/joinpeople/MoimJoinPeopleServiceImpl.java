package us.flower.dayary.service.moim.joinpeople;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.People;
import us.flower.dayary.repository.moim.MoimPeopleRepository;

@Service
public class MoimJoinPeopleServiceImpl implements MoimJoinPeopleService{

	@Autowired
	MoimPeopleRepository moimpeopleRepository;
	@Autowired
	SqlSession sqlSession;
	@Override
	public void deleteMoimPeople(long moimPeopleNo) {
		moimpeopleRepository.deleteById(moimPeopleNo);
	}
	
	@Transactional
	@Override
	public void banMoimpeople(People people, Moim moim) {
		 moimpeopleRepository.deleteByPeopleAndMoim(people,moim);
	}

	@Override
	public List<Long> searchJoinedPeople(long moimNo) {
		
		List<MoimPeople> moimPeopleList = moimpeopleRepository.findByMoim_id(moimNo);
		List<Long> peopleId = new ArrayList<Long>();
		String joinCondition = "N"; //가입승인유무
		
		for (MoimPeople person : moimPeopleList) {
			
			joinCondition = Character.toString(person.getJoinCondition());
			
			if(joinCondition.equals("Y")) {
				peopleId.add(person.getPeople().getId());
			}
		}
		
		return peopleId;
	}

	@Override
	public Map<String, String> selectCountMoimPeople(long moimNo) {
		return sqlSession.selectOne("moimPeople.selectCountMoimPeople",moimNo);
	}

}
