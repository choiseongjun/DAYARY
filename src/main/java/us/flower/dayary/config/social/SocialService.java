package us.flower.dayary.config.social;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import us.flower.dayary.config.social.connection.UserConnection;
import us.flower.dayary.domain.People;
import us.flower.dayary.service.people.PeopleService;

@Component
@Transactional
public class SocialService {

	@Autowired
    private PeopleService peopleService;

    public UsernamePasswordAuthenticationToken doAuthentication(UserConnection userConnection) {

        if (peopleService.isExistUser(userConnection)) {
            // 기존 회원일 경우에는 데이터베이스에서 조회해서 인증 처리
            final People user = peopleService.findBySocial(userConnection);
            return setAuthenticationToken(user);
        } else {
            // 새 회원일 경우 회원가입 이후 인증 처리
            final People user = peopleService.signUp(userConnection);
            return setAuthenticationToken(user);

        }
    }


    private UsernamePasswordAuthenticationToken setAuthenticationToken(Object user) {
        return new UsernamePasswordAuthenticationToken(user, null, getAuthorities("ROLE_USER"));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

}
