package us.flower.dayary.config.social.google;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import us.flower.dayary.config.social.SocialService;
import us.flower.dayary.config.social.connection.UserConnection;

public class GoogleOAuth2ClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

    private ObjectMapper mapper = new ObjectMapper();
    private SocialService socialService;

    public GoogleOAuth2ClientAuthenticationProcessingFilter(SocialService socialService) {
        super("/login/google");
        this.socialService = socialService;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // super.successfulAuthentication(request, response, chain, authResult);
        // Nearly a no-op, but if there is a ClientTokenServices then the token will now be stored
    	
        final OAuth2AccessToken accessToken = restTemplate.getAccessToken();
        final OAuth2Authentication auth = (OAuth2Authentication) authResult;
        final Object details = auth.getUserAuthentication().getDetails();

        final GoogleUserDetails userDetails = mapper.convertValue(details, GoogleUserDetails.class);
        userDetails.setAccessToken(accessToken);
        final UserConnection userConnection = UserConnection.valueOf(userDetails);

        final UsernamePasswordAuthenticationToken authenticationToken = socialService.doAuthentication(userConnection);
        System.out.println("userConnection#@@#!@#");
        System.out.println(userConnection.toString());
       
        super.successfulAuthentication(request, response, chain, authenticationToken);
       
    }

}
