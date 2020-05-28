package us.flower.dayary.config.social.facebook;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import lombok.Getter;

@Getter
public class FacebookUserDetails {

    public Picture picture;
    private String id;
    private String name;
    private String email;
    private long expiration;
    private String access_token;

    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.access_token = accessToken.getValue();
        this.expiration = accessToken.getExpiration().getTime();
    }

    public String getImageUrl() {
        return picture.data.url;
    }

    @Getter
    public static class Picture {
        private Data data;

        public static class Data {
            private String url;
        }
    }


}
