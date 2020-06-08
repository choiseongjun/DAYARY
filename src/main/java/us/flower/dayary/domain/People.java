package us.flower.dayary.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import us.flower.dayary.config.social.connection.UserConnection;
import us.flower.dayary.domain.common.DateAudit;
import us.flower.dayary.oauth2.SocialType;

/**
*회원
* @param 
* @return
* @throws 
* @date Role 추가  2019-09-17
* @author choiseongjun
*/
@Entity
@Table(name="PEOPLE",uniqueConstraints = {
      
            @UniqueConstraint(columnNames = {
                "email"
            })
    })
@Getter
@Setter
@NoArgsConstructor
@ToString
public class People extends DateAudit{
 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;

	@Column(name="EMAIL")
	private String email;

	@Column(name="PASSWORD")
	private String password;

	@Column(name="NAME")
	private String name;
	@Column(name="JOB")
	private String job;
	@Column(name="SEX")
	private String sex;
	@Column(name="INTERESTS")
	private String interests;
	@Column(name="PHOTO")
	private String photo;
	@Lob //길이 제한 없음
	@Column(name="INTRODUCE")
	private String introduce;
	 //이미지경로
   	@Column(name = "IMAGE_PATH", nullable = true)
       private String imagePath;
 
   	//이미지이름
   	@Column(name = "IMAGE_NAME", nullable = true)
       private String imageName;

   	//이미지확장자
   	@Column(name = "IMAGE_EXTENSION", nullable = true)
       private String imageExtension;
  //시/도
    @Column(name = "SIDO_CODE")
     private String sidocode;
    
    //구
    @Column(name = "SIGOON_CODE")
    private String sigooncode;
	
    //생년월일
	@Column(name = "BIRTH")
    private String birth;

	
	@Column(name="ACTIVATION",columnDefinition = "default 'N'")
	private String activation;
	@Column(name="DELETE_YN",nullable=false, columnDefinition = "char(1) default 'N'")
	private char delete_yn;
	@OneToMany(fetch = FetchType.LAZY,orphanRemoval=true,mappedBy = "people")
	@JsonIgnore
	private List<MoimPeople> moimpeople;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_ROLES", 
            joinColumns = @JoinColumn(name = "PEOPLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<Role> roles = new HashSet<>();
    // 스터디 가입된 회원 참여자 
   
    @Column
    private String pincipal;
    
    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    @OneToOne(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = true, updatable = false, unique = true)
    private UserConnection social;
    @Builder
	public People(long id, String email, String password, String name, String photo, String imagePath, String imageName,
			String imageExtension, String activation, String pincipal, SocialType socialType,UserConnection social) {
		super();
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.photo = photo;
		this.imagePath = imagePath;
		this.imageName = imageName;
		this.imageExtension = imageExtension;
		this.activation = activation;
		this.pincipal = pincipal;
		this.socialType = socialType;
		this.social = social;
	}

    
    public People(String email,String password,String name,String photo,String activation,String job,String sex,String interests,String introduce,String birth,String sidocode,String sigooncode) {
    	this.email=email;
    	this.password=password;
    	this.name=name;
    	this.photo=photo;
    	this.activation=activation;
    	this.job = job;
    	this.sex = sex;
    	this.interests = interests;
    	this.introduce = introduce;
    	this.birth = birth;
    	this.sidocode = sidocode;
    	this.sigooncode = sigooncode;
    }
    public static People signUp(UserConnection userConnection) {

        return People.builder()
                .email(userConnection.getEmail())
                .name(userConnection.getDisplayName())
                .social(userConnection)
                .build();

    }
	
}
