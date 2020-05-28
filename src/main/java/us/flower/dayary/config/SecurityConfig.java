
package us.flower.dayary.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import us.flower.dayary.config.auth.ClientResources;
import us.flower.dayary.config.handler.LoggingAccessDeniedHandler;
import us.flower.dayary.config.social.SocialService;
import us.flower.dayary.config.social.facebook.FacebookOAuth2ClientAuthenticationProcessingFilter;
import us.flower.dayary.config.social.google.GoogleOAuth2ClientAuthenticationProcessingFilter;
import us.flower.dayary.security.CustomLoginSuccessHandler;
import us.flower.dayary.security.CustomUserDetailsService;
import us.flower.dayary.security.JwtAuthenticationEntryPoint;
import us.flower.dayary.security.JwtAuthenticationFilter;

/**
 * 시큐리티 설정 2019-09월초 by 최성준
 */
@Configuration
@EnableWebSecurity
@EnableOAuth2Client
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
	private OAuth2ClientContext oauth2ClientContext;
    @Autowired
    private SocialService socialService;
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;
	@Autowired
    private LoggingAccessDeniedHandler accessDeniedHandler;
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public AuthenticationSuccessHandler successHandler() {
	    return new CustomLoginSuccessHandler("/loginSuccess");
	}
	private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(google(), new GoogleOAuth2ClientAuthenticationProcessingFilter(socialService)));
        filters.add(ssoFilter(facebook(), new FacebookOAuth2ClientAuthenticationProcessingFilter(socialService)));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(ClientResources client, OAuth2ClientAuthenticationProcessingFilter filter) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(restTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(), client.getClient().getClientId());
        filter.setTokenServices(tokenServices);
        tokenServices.setRestTemplate(restTemplate);
        return filter;
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers(
                 "/",
                 "/js/**",
                 "/css/**",
                 "/img/**",
                 "/images/**",
                 "/publ/**").permitAll()
		.antMatchers(
			       "/signup").permitAll()
		.antMatchers("/admin/**").access("hasAnyRole('ADMIN')")
		.antMatchers("/moimMakeView").access("hasAnyRole('USER')")
		.antMatchers("/moimlistView/moimdetailView/**").access("hasAnyRole('USER')")
		.and().formLogin().  //login configuration
                loginPage("/signinView").
                failureUrl("/loginerror").
                loginProcessingUrl("/appLogin").
                successHandler(new CustomLoginSuccessHandler("/")).
                usernameParameter("email").
                passwordParameter("password").
                defaultSuccessUrl("/loginSuccess").
                successHandler(successHandler()).
		 and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
		 .logout()    //logout configuration
		.logoutUrl("/logout")
		.invalidateHttpSession(true)
		.clearAuthentication(true)
		.and()
		.exceptionHandling()
		.accessDeniedPage("/access-denied"); // 권한이 없을경우 해당 url로 이동

	;

		// Add our custom JWT security filter
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

	}
	 @Bean
	 public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
	     FilterRegistrationBean registration = new FilterRegistrationBean();
	     registration.setFilter(filter);
	     registration.setOrder(-100);
	     return registration;
	 }
	@Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("google")
    public ClientResources google() {
        return new ClientResources();
    }
    
}
