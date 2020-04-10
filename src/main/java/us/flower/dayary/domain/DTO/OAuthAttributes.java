//package us.flower.dayary.domain.DTO;
//
//import java.util.Map;
//
//import lombok.Builder;
//import lombok.Data;
//import us.flower.dayary.domain.User;
//
//@Data
//public class OAuthAttributes {
//
//	private Map<String, Object> attributes;
//	private String nameAttributeKey;
//	private String name;
//	private String email;
//	private String picture;
//	
//	@Builder
//	public OAuthAttributes(Map<String, Object> attributes,
//			String nameAttributeKey, String name, String email, String picture) {
//		this.attributes = attributes;
//		this.nameAttributeKey = nameAttributeKey;
//		this.name = name;
//		this.email = email;
//		this.picture = picture;
//	}
//	
//	public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
//		return ofGoogle(userNameAttributeName, attributes);
//	}
//	
//	
//	public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
//		
//		return OAuthAttributes.builder()
//				.email((String) attributes.get("email"))
//				;
//	}
//	
//	public User toEntity() {
//		return User.builder()
//				.name(this.name)
//				.email(this.email)
//				.picture(this.picture)
//				;
//	}
//	
//}
