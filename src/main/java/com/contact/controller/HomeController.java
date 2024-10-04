package com.contact.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.entities.User;
import com.contact.helper.Message;
import com.contact.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Home - Smart Contact Manager");
		return "index";
	}

	@GetMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title", "Signup - Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";
	}

	@GetMapping("/login")
	public String login(Model m) {
		m.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}
//	@GetMapping("/logout")
//	public String logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
////		Cookie[] cookies = request.getCookies();
////		for (Cookie c : cookies) {
////			if (c.getName().equals("JSESSIONID")) {
////				c.setValue(null);
////			}
////		}
////		request.getSession().invalidate();
//		
//		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
//		logoutHandler.logout(request, response, authentication);
//		
//
//		return "redirect:/";
//	}

	@PostMapping("/signup")
	public String createAccount(@Valid @ModelAttribute User user, BindingResult result, @RequestParam(defaultValue = "false") boolean agreement,
			Model model, HttpSession session) {

		try {
			if (!agreement) {
				throw new Exception("Please agree to terms and conditions");
			}

			if (result.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setImageUrl("default-contact.png");
			userService.addUser(user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Registered Successfully", "alert-success"));
			return "signup";
		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!", "alert-danger"));
			return "signup";
		}

	}
}
