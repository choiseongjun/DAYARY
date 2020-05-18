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
import us.flower.dayary.domain.MoimTag;
import us.flower.dayary.domain.People;
import us.flower.dayary.domain.Tag;
import us.flower.dayary.domain.ToDoWrite;
import us.flower.dayary.domain.DTO.TempData;
import us.flower.dayary.repository.CommonRepository;
import us.flower.dayary.repository.moim.HashTagRepository;
import us.flower.dayary.repository.moim.MoimPeopleRepository;
import us.flower.dayary.repository.moim.MoimRepository;
import us.flower.dayary.repository.moim.MoimTagRepository;
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
	private HashTagRepository hashTagRepository;
	@Autowired
	private MoimTagRepository moimTagRepository;
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
       
        //�궗吏꾩씠�엳�떎硫�
        if(file!=null) {
        	
        	//�씠誘몄��뙆�씪�씠由꾩깮�꽦
	        String imageName="";
			while(true){
	        	imageName=tokenGenerator.getToken();
				//DB�뿉 �뙆�씪�씠由꾩씠 議댁옱�븯吏� �븡�쑝硫� moim domain�뿉 set
	        	if(!moimRepository.existsByImageName(imageName)){
					moim.setImageName(imageName);
					break; 
				}
			}
	  
	        //�씠誘몄��뙆�씪�솗�옣�옄異붿텧
	        String originalFileName = file.getOriginalFilename();
	        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
	        moim.setImageExtension(fileExtension);
	
	        //�뙆�씪�뾽濡쒕뱶
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
        
        //�빐�떆�깭洹�
        String hashtag = moim.getHashtag();
		String[] hashtagArr = hashtag.split("#");
		
		for(int i=0;i<hashtagArr.length;i++) {
			if(!hashtagArr[i].isEmpty()) {
				Tag tag =new Tag();	
				tag.setName(hashtagArr[i]);
				
				MoimTag moimtag = new MoimTag();
				moimtag.setTag(tag);
				moimtag.setMoim(moim);
				
				
				hashTagRepository.save(tag);
				moimTagRepository.save(moimtag);
			}
		}
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
			moimPeople.setMaker(maker);//Y媛� �꽆�뼱�삩寃쎌슦 紐⑥엫�옣�씤嫄곗� �씪諛섏쑀�� 援щ텇�븯湲곗쐞�븿
		}else {
			moimPeople.setMaker(maker);//Y媛� �꽆�뼱�삩寃쎌슦 紐⑥엫�옣�씤嫄곗� �씪諛섏쑀�� 援щ텇�븯湲곗쐞�븿
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

	// [2020.01.28][hyozkim] 異붽�
	@Override
	public Page<Moim> selectMoimByCategory(Pageable pageable, String commonCode) {
		return moimRepository.findAll(pageable);
		// �닔�젙 �븘�슂
		//return moimRepository.findByCommCode(pageable,commonCode);
	}

//	@Override
//	public Page<Moim> selecttitleList(Pageable pageable, String title) {
//		return moimRepository.findAllByTitleLike(pageable,"%"+title+"%");
//	}

	@Override
	public Page<Moim> selecttitleList(Pageable pageable, String title, String sido_code,String sigoon_code) {
		
		if(title=="") {
			return moimRepository.findAllBySidocodeLikeAndSigooncodeLike(pageable,"%"+sido_code+"%","%"+sigoon_code+"%");
		}else {
			return moimRepository.findAllDistinctByMoimtagTagNameLikeAndSidocodeLikeAndSigooncodeLike(pageable,"%"+title+"%","%"+sido_code+"%","%"+sigoon_code+"%");	
		}
		
	}

	@Override
	public void updateMoim(String email, Moim moim, MultipartFile file) {
		   People people = peopleRepository.findByEmail(email);
	        //Common category=commonRepository.figetMoimElementndBycommName(subject);
	       
	        //�궗吏꾩씠�엳�떎硫�
	        if(file!=null) {
	        	
	        	//�씠誘몄��뙆�씪�씠由꾩깮�꽦
		        String imageName="";
				while(true){
		        	imageName=tokenGenerator.getToken();
					//DB�뿉 �뙆�씪�씠由꾩씠 議댁옱�븯吏� �븡�쑝硫� moim domain�뿉 set
		        	if(!moimRepository.existsByImageName(imageName)){
						moim.setImageName(imageName);
						break; 
					}
				}
		  
		        //�씠誘몄��뙆�씪�솗�옣�옄異붿텧
		        String originalFileName = file.getOriginalFilename();
		        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
		        moim.setImageExtension(fileExtension);
		
		        //�뙆�씪�뾽濡쒕뱶
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
	        moimTagRepository.deleteByMoim(moim);
	    	System.out.println("ok");
	        //�빐�떆�깭洹�
	        String hashtag = moim.getHashtag();
			String[] hashtagArr = hashtag.split("#");
			
			for(int i=0;i<hashtagArr.length;i++) {
				System.out.println(hashtagArr[i]+"////");
				if(!hashtagArr[i].isEmpty()) {
					Tag tag =hashTagRepository.findByName(hashtagArr[i]);
					if(tag==null) {
						tag=new Tag();
						tag.setName(hashtagArr[i]);
						hashTagRepository.save(tag);
					}
				
					MoimTag moimtag = new MoimTag();
					moimtag.setTag(tag);
					moimtag.setMoim(moim);
					moimTagRepository.save(moimtag);
				}
			}
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
		return moimRepository.findAllByCategory(common,pageable);
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
		if(title=="") {
			return moimRepository.findAllByCategoryAndSidocodeLikeAndSigooncodeLike(pageable,common,"%"+sido_code+"%","%"+sigoon_code+"%");
		}else {
			return moimRepository.findAllDistinctByMoimtagTagNameLikeAndCategoryAndSidocodeLikeAndSigooncodeLike(pageable,"%"+title+"%",common,"%"+sido_code+"%","%"+sigoon_code+"%");
		}
	}
	

	
}