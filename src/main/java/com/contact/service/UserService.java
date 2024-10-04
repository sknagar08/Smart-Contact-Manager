package com.contact.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contact.entities.User;
import com.contact.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	public User addUser(User user) {
		return userRepository.save(user);
	}

	public User getUserByUserName(String email) {
		return userRepository.getUserByUserName(email);
	}
}
