package us.flower.dayary.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import us.flower.dayary.domain.People;
/**
*
* @param 
* @return
* @throws 
* @date 2019-09-17
* @author choiseongjun
*/
public class UserPrincipal implements UserDetails {
	
	private Long id;
	@JsonIgnore
    private String email;

    private String name;


    @JsonIgnore
    private String password;

    private String photo;

	private String activation;

    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(long id,String email,String name, String password,String photo,String activation, Collection<? extends GrantedAuthority> authorities) {
    	this.id=id;
    	this.email = email;
        this.name = name;
        this.password = password;
        this.photo=photo;
        this.activation=activation;
        this.authorities = authorities;
    }

    public static UserPrincipal create(People user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());

        return new UserPrincipal(
        		user.getId(),
        		user.getEmail(),
                user.getName(),
                user.getPassword(),
                user.getPhoto(),
                user.getActivation(),
                authorities
        );
    }

    public Long getId() {
		return id;
	}
   
    
    public String getName() {
        return name;
    }

	public String getPhoto() {
		return photo;
	}
	public String getActivation() {
		return activation;
	}

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

	@Override
	public String getUsername() {
		return name;
	}
}