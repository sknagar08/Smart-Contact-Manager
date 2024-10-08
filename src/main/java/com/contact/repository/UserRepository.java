package com.contact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("select u from User u where u.email = :email")
	User getUserByUserName(@Param(value = "email") String email);
	
}
