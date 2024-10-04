package com.contact.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contact.entities.Contact;
import com.contact.entities.User;
import com.contact.helper.Message;
import com.contact.service.ContactService;
import com.contact.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ContactService contactService;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String username = principal.getName();
		User curUser = userService.getUserByUserName(username);

		model.addAttribute("user", curUser);
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("title", "Dashboard");
		return "normal/user_dashboard";
	}

	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile");
		return "normal/user_profile";
	}

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add New Contact");
		model.addAttribute("form_type", "add");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/add-contact")
	public String processAddContact(Model model, @ModelAttribute Contact contact,
			@RequestParam MultipartFile profileImage, Principal principal) {

		try {
			String username = principal.getName();
			User curUser = userService.getUserByUserName(username);
			contact.setUser(curUser);

			if (profileImage.isEmpty()) {
				contact.setImageUrl("default-contact.png");
			} else {
				String fileName = UUID.randomUUID() + profileImage.getOriginalFilename();
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
				Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImageUrl(fileName);
			}

			curUser.getContacts().add(contact);
			userService.addUser(curUser);

			model.addAttribute("message", new Message("Contact added successfully!", "success"));

			model.addAttribute("title", "Add New Contact");
			model.addAttribute("form_type", "add");
			model.addAttribute("contact", new Contact());
		} catch (Exception e) {
			model.addAttribute("message", new Message("Some error occured! Try Again!", "danger"));
			e.printStackTrace();
		}
		return "normal/add_contact_form";
	}

	@GetMapping("/contacts/{curPage}")
	public String showContacts(@PathVariable int curPage, Model model, Principal principal) {
		String username = principal.getName();
		User curUser = userService.getUserByUserName(username);

		if (curPage < 0) {
			this.showContacts(0, model, principal);
		}

		Pageable pageable = PageRequest.of(curPage, 10);

		Page<Contact> contacts = contactService.getAllContactsByUser(curUser.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("curPage", curPage);
		model.addAttribute("totalPages", contacts.getTotalPages());
		model.addAttribute("title", "All Contacts");
		return "normal/show_contacts";
	}

	@GetMapping("/{id}/contact")
	public String getContactDetail(@PathVariable long id, Model model, Principal principal) {

		Optional<Contact> optContact = contactService.getContactById(id);
		String username = principal.getName();
		User curUser = userService.getUserByUserName(username);

		if (optContact.isPresent()) {
			Contact contact = optContact.get();

			if (curUser.getId() == contact.getUser().getId())
				model.addAttribute("contact", contact);

			model.addAttribute("error", "403 - Unauthorized");
			model.addAttribute("title", contact.getName() + " - Smart Contact Manager");
		} else {
			model.addAttribute("error", "Contact you are looking for is not present");
			model.addAttribute("title", "Smart Contact Manager");
		}
		return "normal/contact_details";
	}

	@GetMapping("/contact/delete/{id}")
	public String deleteContact(@PathVariable long id, Principal principal, Model model) throws IOException {
		Optional<Contact> optContact = contactService.getContactById(id);

		String username = principal.getName();
		User curUser = userService.getUserByUserName(username);

		if (optContact.isPresent()) {
			Contact contact = optContact.get();

			if (curUser.getId() == contact.getUser().getId()) {
				// deleting photo
				if (!contact.getImageUrl().equals("default-contact.png")) {
					File file = new ClassPathResource("static/images").getFile();
					Path path = Paths.get(file.getAbsolutePath() + File.separator + contact.getImageUrl());
					Files.deleteIfExists(path);
				}
				contactService.deleteContact(contact.getId());
				return "redirect:/user/contacts/0";
			}
		}

		model.addAttribute("title", "Error");
		model.addAttribute("message", new Message("Requested contact is not present!", "danger"));
		return "normal/error";
	}

	@GetMapping("/update-contact/{id}")
	public String openUpdateContactForm(@PathVariable long id, Model model, Principal principal) {
		Optional<Contact> optContact = contactService.getContactById(id);

		String username = principal.getName();
		User curUser = userService.getUserByUserName(username);

		if (optContact.isPresent()) {
			Contact contact = optContact.get();

			if (curUser.getId() == contact.getUser().getId()) {
				model.addAttribute("title", "Update Contact");
				model.addAttribute("form_type", "update");
				model.addAttribute("contact", contact);
				return "normal/add_contact_form";
			}
		}

		model.addAttribute("title", "Error");
		model.addAttribute("message", new Message("Requested contact is not present!", "danger"));
		return "normal/error";
	}

	@PostMapping("/update-contact/{id}")
	public String updateContact(@PathVariable long id, @ModelAttribute Contact contact,
			@RequestParam MultipartFile profileImage, Model model, Principal principal) {
		try {
			Optional<Contact> optContact = contactService.getContactById(id);
			if (optContact.isPresent()) {
				Contact exContact = optContact.get();

				if (!profileImage.isEmpty()) {
					File file = new ClassPathResource("static/images").getFile();
					// delete old photo
					if (!exContact.getImageUrl().equals("default-contact.png")) {
						Path path = Paths.get(file.getAbsolutePath() + File.separator + exContact.getImageUrl());
						Files.deleteIfExists(path);
					}
					// update new photo
					String fileName = UUID.randomUUID() + profileImage.getOriginalFilename();
					Path path = Paths.get(file.getAbsolutePath() + File.separator + fileName);
					Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					contact.setImageUrl(fileName);
				} else {
					contact.setImageUrl(exContact.getImageUrl());
				}

				User curUser = userService.getUserByUserName(principal.getName());
				contact.setId(id);
				contact.setUser(curUser);
				contactService.addContact(contact);
				return "redirect:/user/" + id + "/contact";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("title", "Error");
		model.addAttribute("message", new Message("Requested contact is not present!", "danger"));
		return "normal/error";
	}

	@GetMapping("/edit-profile")
	public String editProfileForm(Model model) {
		model.addAttribute("title", "Edit Profile");
		return "normal/edit_profile";
	}

	@PostMapping("/edit-profile")
	public String editProfile(@ModelAttribute User user, @RequestParam MultipartFile profileImage) throws IOException {
		if (!profileImage.isEmpty()) {
			File file = new ClassPathResource("static/images").getFile();

			if (!user.getImageUrl().equals("default-contact.png")) {
				String prevProfilePhoto = user.getImageUrl();
				Path path = Paths.get(file.getAbsolutePath() + File.separator + prevProfilePhoto);
				Files.deleteIfExists(path);
			}

			String fileName = UUID.randomUUID() + profileImage.getOriginalFilename();
			Path path = Paths.get(file.getAbsolutePath() + File.separator + fileName);
			Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			user.setImageUrl(fileName);
		}

		userService.addUser(user);

		return "redirect:/user/profile";
	}

}
