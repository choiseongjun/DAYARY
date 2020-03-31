package us.flower.dayary.common;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


import java.util.Properties;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * * * @author jiheon * * @see <a *
 * href="http://www.thymeleaf.org/doc/articles/springmail.html">http://www.thymeleaf.org/doc/articles/springmail.html</a>
 * * @see <a *
 * href="http://stackoverflow.com/questions/25610281/spring-boot-sending-emails-using-thymeleaf-as-template-configuration-does-not">http://stackoverflow.com/questions/25610281/spring-boot-sending-emails-using-thymeleaf-as-template-configuration-does-not</a>
 */ 

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
@PropertySource(value="classpath:application.yml")
@ToString
public class emailManager {
	private static final String MAIL_DEBUG = "mail.debug";
	private static final String MAIL_SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
	private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

	@Data
	public static class Smtp {
		private boolean auth;
		private boolean startTlsRequired;
		private boolean startTlsEnable;
	}

	@NotBlank
	private String host;
	private String protocol;
	private int port;
	private String username;
	private String password;
	private String defaultEncoding;
	@NotNull
	private Smtp smtp;

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(getHost());
		mailSender.setProtocol(getProtocol());
		mailSender.setPort(getPort());
		mailSender.setUsername(getUsername());
		mailSender.setPassword(getPassword());
		mailSender.setDefaultEncoding(getDefaultEncoding());
		Properties properties = mailSender.getJavaMailProperties();
		properties.put(MAIL_SMTP_STARTTLS_REQUIRED, getSmtp().isStartTlsRequired());
		properties.put(MAIL_SMTP_STARTTLS_ENABLE, getSmtp().isStartTlsEnable());
		properties.put(MAIL_SMTP_AUTH, getSmtp().isAuth());
		properties.put(MAIL_DEBUG, true);
		mailSender.setJavaMailProperties(properties);
		return mailSender;
	}



}

