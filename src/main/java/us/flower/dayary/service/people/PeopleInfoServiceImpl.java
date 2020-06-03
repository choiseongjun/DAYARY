package us.flower.dayary.service.people;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import us.flower.dayary.common.FileManager;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.DTO.TempData;
import us.flower.dayary.repository.people.PeopleRepository;

@Service
public class PeopleInfoServiceImpl implements PeopleInfoService{
	
	@Autowired
	PeopleRepository peopleRepository;
	@Autowired
	SqlSession sqlSession;

	@Value("${moimImagePath}")
	private String moimImagePath;
	@Autowired
	private FileManager fileManager;
	@Override
	public byte[] getMoimImage(String imageName) throws Exception {
		return fileManager.getByteArray(moimImagePath+"/"+imageName);
	}
	@Override
	public void deletePeople(long peopleId) {
		
		Optional<People> people = peopleRepository.findById(peopleId);
		people.ifPresent(selectUser->{
			selectUser.setDelete_yn('Y');
			peopleRepository.save(selectUser);
		});
		//peopleRepository.deleteById(peopleId);
	}
	@Override
	public List<TempData> MyTodoProgress(long no) {
		return sqlSession.selectList("todo.selectMyTodoProgress",no);
	}

}
