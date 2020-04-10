package us.flower.dayary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import us.flower.dayary.domain.common.DateAudit;
@Entity
@Table(name = "MENU")
@Data
public class Menu extends DateAudit{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;
	
	//메뉴명
    @Column(name = "MENU_NAME")
    private String menu_name;
	//메뉴url
    @Column(name = "MENU_URL")
    private String menu_url;
	//메뉴내용
    @Column(name = "MENU_MEMO")
    private String menu_memo;
	//메뉴구분
    @Column(name = "MENU_GB")
    private String menu_gb;
	//메뉴순서
    @Column(name = "MENU_ORDER")
    private long menu_order;
    //삭제여부
    @Column(name = "DEL_YN")
    private char del_yn;
    //최초 생성자
    @Column(name = "CREATED_USER")
    private String created_user;
    //수정자
    @Column(name = "UPDATED_USER")
    private String updated_user;
}

