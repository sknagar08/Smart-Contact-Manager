package com.contact.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.contact.entities.Contact;
import com.contact.entities.User;
import com.contact.repository.ContactRepository;

@Service
public class ContactService {
	@Autowired
	private ContactRepository contactRepository;

	public Contact addContact(Contact contact) {
		return contactRepository.save(contact);
	}

	public Page<Contact> getAllContactsByUser(long userId, Pageable pageable) {
		return contactRepository.getContactsByUser(userId, pageable);
	}
	
	public Optional<Contact> getContactById(long id) {
		return contactRepository.findById(id);
	}
	
	public void deleteContact(long id) {
		contactRepository.deleteById(id);
	}
	
	public List<Contact> getContactsByNameContainingAndUser(String name, User user){
		return contactRepository.findByNameContainingIgnoreCaseAndUserOrderByName(name, user);
	}

}
