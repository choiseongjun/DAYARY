package us.flower.dayary.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import us.flower.dayary.domain.People;
import us.flower.dayary.repository.people.PeopleRepository;

@Service 
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	PeopleRepository peopleRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		People user = peopleRepository.findByEmail(email);
		return UserPrincipal.create(user);
	}
 
	public UserDetails loadUserById(Long id) {
		  People user = peopleRepository.findById(id).orElseThrow(
		            () -> new UsernameNotFoundException("User not found with id : " + id)
		        );
		  return UserPrincipal.create(user);
		  
	}

	

	

}