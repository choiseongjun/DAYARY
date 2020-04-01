
package us.flower.dayary.config;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import us.flower.dayary.config.handler.LoggingAccessDeniedHandler;
import us.flower.dayary.security.CustomLoginSuccessHandler;
import us.flower.dayary.security.CustomUserDetailsService;
import us.flower.dayary.security.JwtAuthenticationEntryPoint;
import us.flower.dayary.security.JwtAuthenticationFilter;

/**
 * 시큐리티 설정 2019-09월초 by 최성준
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
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
                 "/images/**").permitAll()
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
		 and().logout()    //logout configuration
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
	public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties) {
		List<ClientRegistration> registrations = oAuth2ClientProperties.getRegistration().keySet().stream()
				.map(client -> getRegistration(oAuth2ClientProperties, client)).filter(Objects::nonNull)
				.collect(Collectors.toList());

		return new InMemoryClientRegistrationRepository(registrations);
	}

	private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties, String client) {
		if ("google".equals(client)) {
			OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("google");
			return CommonOAuth2Provider.GOOGLE.getBuilder(client).clientId(registration.getClientId())
					.clientSecret(registration.getClientSecret()).scope("email", "profile").build();
		}
		return null;
	}
}
