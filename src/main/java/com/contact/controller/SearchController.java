package com.contact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contact.entities.Contact;
import com.contact.entities.User;
import com.contact.service.ContactService;
import com.contact.service.UserService;

@RestController
public class SearchController {

	@Autowired
	private UserService userService;

	@Autowired
	private ContactService contactService;

	@GetMapping("/search/{query}")
	public ResponseEntity<List<Contact>> search(@PathVariable String query, Principal principal) {
		User curUser = userService.getUserByUserName(principal.getName());
		List<Contact> contacts = contactService.getContactsByNameContainingAndUser(query, curUser);
		return ResponseEntity.ok(contacts);
	}
}
