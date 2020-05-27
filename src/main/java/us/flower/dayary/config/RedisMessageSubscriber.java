package us.flower.dayary.config;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class RedisMessageSubscriber implements MessageListener {
	 
    static final Logger L = LoggerFactory.getLogger(RedisMessageSubscriber.class);
 

	@Override
	public void onMessage(Message message, byte[] pattern) {
		   // message protocol : value1/value2
        L.debug("subscriber : message [{}]", message.toString());
        StringTokenizer st = new StringTokenizer(message.toString().replaceAll("\"", ""), "/");
        String value1 = st.nextToken();
        String value2 = st.nextToken();
        System.out.println(value1 + ", " + value2);
	}
}
