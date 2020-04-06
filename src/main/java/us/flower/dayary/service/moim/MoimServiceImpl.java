package us.flower.dayary.service.moim;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import us.flower.dayary.common.FileManager;
import us.flower.dayary.common.TokenGenerator;
import us.flower.dayary.domain.Common;
import us.flower.dayary.domain.Moim;
import us.flower.dayary.domain.MoimPeople;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.ToDoWrite;
import us.flower.dayary.domain.DTO.TempData;
import us.flower.dayary.repository.CommonRepository;
import us.flower.dayary.repository.moim.MoimPeopleRepository;
import us.flower.dayary.repository.moim.MoimRepository;
import us.flower.dayary.repository.moim.MoimRepositoryCustom;
import us.flower.dayary.repository.people.PeopleRepository;

@Service
@Transactional
public class MoimServiceImpl implements moimService{

	@Value("${moimImagePath}")
	private String moimImagePath;

	@Autowired
    private PeopleRepository peopleRepository;
	@Autowired
    private MoimRepository moimRepository;
	@Autowired
    private CommonRepository commonRepository;
	@Autowired
	private MoimPeopleRepository moimpeopleRepository;
	
	@Autowired
    private TokenGenerator tokenGenerator;
	@Autowired
	private FileManager fileManager;

	public Map<String, Object> getMoimElement(){
		// List<Common> cateList= (List<Common>) commonRepository.findAll();
		List<Common> elementCA1 = (List<Common>) commonRepository.findByCommHead("CA1");
		List<Common> elementCA2 = (List<Common>) commonRepository.findByCommHead("CA2");
		List<Common> elementCA3 = (List<Common>) commonRepository.findByCommHead("CA3");

		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("element_CA1", elementCA1);
		returnMap.put("element_CA2", elementCA2);
		returnMap.put("element_CA3", elementCA3);

		return returnMap;
	}
	
    public void saveMoim(String email, String subject, Moim moim, MultipartFile file) {

        People people = peopleRepository.findByEmail(email);
        Common category=commonRepository.findBycommName(subject);
       
        //사진이있다면
        if(file!=null) {
        	
        	//이미지파일이름생성
	        String imageName="";
			while(true){
	        	imageName=tokenGenerator.getToken();
				//DB에 파일이름이 존재하지 않으면 moim domain에 set
	        	if(!moimRepository.existsByImageName(imageName)){
					moim.setImageName(imageName);
					break; 
				}
			}
	  
	        //이미지파일확장자추출
	        String originalFileName = file.getOriginalFilename();
	        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
	        moim.setImageExtension(fileExtension);
	
	        //파일업로드
	        try { 
	            fileManager.fileUpload(file, moimImagePath+"/"+imageName+"."+fileExtension);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
    	}

        moim.setPeople(people);
        moim.setCategory(category);
        moim.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        moim.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));

        moimRepository.save(moim);
    }
 

	public Optional<Moim> findMoimone(long no) {
		return moimRepository.findById(no);
	}

    public byte[] getMoimImage(String imageName) throws Exception {
        return fileManager.getByteArray(moimImagePath+"/"+imageName);
    }

	public MoimPeople moimParticipant(long peopleId, long moimId,char joinCondition,char maker) {
		
		
		Moim moim=new Moim();
		moim.setId(moimId);
		
		People people=new People();
		people.setId(peopleId);
		
		MoimPeople moimPeople=new MoimPeople();
		moimPeople.setMoim(moim);
		moimPeople.setPeople(people);
		moimPeople.setJoinrole("study"); 
		moimPeople.setJoinCondition(joinCondition);
		if(maker=='Y') {
			moimPeople.setMaker(maker);//Y가 넘어온경우 모임장인거와 일반유저 구분하기위함
		}else {
			moimPeople.setMaker(maker);//Y가 넘어온경우 모임장인거와 일반유저 구분하기위함
		}
		
		return moimpeopleRepository.save(moimPeople);
	}

	@Override
	@Transactional
	public void deleteMoimOne(long moimNo) {
		moimRepository.deleteById(moimNo);
	}

	@Override
	public String findPeopleOne(Long people_no) {
		return peopleRepository.findPeopleOne(people_no);
	}


//	@Override
//	public Page<Moim> selectListAll(Pageable pageable) {
//		return moimRepository.findAll(pageable);
//	}

	@Override
	public String findMoimPeopleNoOne(long peopleId, long no) {
		return peopleRepository.findMoimPeopleNoOne(peopleId,no);
	}

	@Override
	public List<Moim> findByTitle(String name) {
		return  moimRepository.findByTitleLike("%"+name+"%");
	}

	

	@Override
	public Page<Moim> selectMoimAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return moimRepository.findAll(pageable);
	}

	// [2020.01.28][hyozkim] 추가
	@Override
	public Page<Moim> selectMoimByCategory(Pageable pageable, String commonCode) {
		return moimRepository.findAll(pageable);
		// 수정 필요
		//return moimRepository.findByCommCode(pageable,commonCode);
	}

//	@Override
//	public Page<Moim> selecttitleList(Pageable pageable, String title) {
//		return moimRepository.findAllByTitleLike(pageable,"%"+title+"%");
//	}

	@Override
	public Page<Moim> selecttitleList(Pageable pageable, String title, String sido_code,String sigoon_code) {
		// TODO Auto-generated method stub
		return moimRepository.findAllByTitleLikeAndSidocodeLikeAndSigooncodeLike(pageable,"%"+title+"%","%"+sido_code+"%","%"+sigoon_code+"%");
	}

	@Override
	public void updateMoim(String email, Moim moim, MultipartFile file) {
		   People people = peopleRepository.findByEmail(email);
	        //Common category=commonRepository.figetMoimElementndBycommName(subject);
	       
	        //사진이있다면
	        if(file!=null) {
	        	
	        	//이미지파일이름생성
		        String imageName="";
				while(true){
		        	imageName=tokenGenerator.getToken();
					//DB에 파일이름이 존재하지 않으면 moim domain에 set
		        	if(!moimRepository.existsByImageName(imageName)){
						moim.setImageName(imageName);
						break; 
					}
				}
		  
		        //이미지파일확장자추출
		        String originalFileName = file.getOriginalFilename();
		        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
		        moim.setImageExtension(fileExtension);
		
		        //파일업로드
		        try { 
		            fileManager.fileUpload(file, moimImagePath+"/"+imageName+"."+fileExtension);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        
	    	}

	        String title = moim.getTitle();
	        long moimId = moim.getId();
	        String intro = moim.getIntro();
	        long peopleLimit = moim.getPeopleLimit();
	        char joincondition = moim.getJoinCondition();
	        String imageName=moim.getImageName();
	        String imageExtension=moim.getImageExtension();

	        moimRepository.updateMoim(title,intro,peopleLimit,joincondition,imageName,imageExtension,moimId);
	}

	@Override
	public long selectMaxMoimId() {
		return moimRepository.selectMaxMoimId();
	}

	@Override
	@Transactional(readOnly = false)
	public void updateMoimClosed(int moimNo) {
		moimRepository.updateMoimClosed("Y",moimNo);
	}

	@Override
	public Page<Moim> selectMoimCate(Pageable pageable, String commCode) {
		
		Common common =new Common();
		common.setCommCode(commCode);
		//return moimRepository.findAllByCategory(common,pageable);
		return moimRepository.findAllByCategoryQuery(common,pageable);
	}
	@Autowired
	SqlSession sqlSession;
	
	@Override
	public List<ToDoWrite> selectTodoLankChart(long no) {
		return sqlSession.selectList("todo.selecttodoStatusGroup",no);
	}

	@Override
	public List<TempData> selectTodoCompltLankChart(long no) {
		return sqlSession.selectList("todo.selectTodoCompltLankChart",no);
	}

	@Override
	public List<TempData> TodotimeLinelist(long no) {
		return sqlSession.selectList("todo.selectTodotimeLinelist",no);
	}

	@Override
	public Page<Moim> selectseacrhList(Pageable pageable, String title, String sido_code, String sigoon_code,
			String commCode) {
		Common common =new Common();
		common.setCommCode(commCode);
		
		return moimRepository.findAllByCategoryAndTitleLikeAndSidocodeLikeAndSigooncodeLike(pageable,common,"%"+title+"%","%"+sido_code+"%","%"+sigoon_code+"%");
	}
	

	
}