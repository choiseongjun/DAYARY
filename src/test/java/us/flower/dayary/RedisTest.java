package us.flower.dayary;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
 
	 @Autowired
	    RedisTemplate<String, Object> redisTemplate;
	    @Autowired
	    Environment env;
	 
	    @Test
	    public void redisPubSub_guide() {
	        //protocol : value1/value2
	        String message = "foo/bar";
	        String channel = env.getProperty("spring.redis.channel");
	        redisTemplate.convertAndSend(channel, message);
	    }
}
