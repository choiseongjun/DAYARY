package us.flower.dayary;

import java.io.File;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties
@EnableAspectJAutoProxy
public class DayaryApplication {

	
	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	
    @Value("${moimImagePath}")
    private String moimImagePath;

	@Value("${communityImagePath}")
    private String communityImagePath;

    public static void main(String[] args) {
        SpringApplication.run(DayaryApplication.class, args);
    }

    //모임이미지디랙토리생성
    @PostConstruct
    public void makeDirectory() {
        File moimImagePath = new File(this.moimImagePath);
        if(!moimImagePath.exists()){
            moimImagePath.mkdirs();
        }

        // make community image root directory
        File communityImagePath = new File(this.communityImagePath);
        if(!communityImagePath.exists()){
            communityImagePath.mkdirs();
        }
    }

    @Bean(name="communityImagePath")
    public String communityImagePath(){
        return communityImagePath;
    }

    @Bean(name="moimImagePath")
    public String moimImagePath(){
        return  moimImagePath;
    }
   
}
