package com.contact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contact.entities.User;
import com.contact.service.UserService;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userService.getUserByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("Could not found user with username: " + username);
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		return customUserDetails;
	}

}
