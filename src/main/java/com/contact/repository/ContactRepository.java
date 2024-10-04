package com.contact.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.Contact;
import com.contact.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Long> {
	@Query("from Contact c where c.user.id = :userId order by c.name")
	Page<Contact> getContactsByUser(@Param("userId") long userId, Pageable pageable);
	
	public List<Contact> findByNameContainingIgnoreCaseAndUserOrderByName(String name, User user);
}
