package com.project.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.model.CreateUser;
import com.project.model.Credential;
import com.project.model.User;
import com.project.repos.UserRepository;
import com.project.services.SecurityService;

@RestController
public class UserController {

	@Autowired
	private UserRepository userrepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private SecurityService securityService;

	@GetMapping("/profile")
	public ResponseEntity<User> showProfile() {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/signup")
	public ResponseEntity<User> register(@Valid @RequestBody CreateUser createUser, BindingResult bindingResult) {
		if (bindingResult.hasErrors())
			return ResponseEntity.badRequest().build();
		User user = new User();
		user.setFirstName(createUser.getFirstName());
		user.setLastName(createUser.getLastName());
		User findUser = userrepository.findByEmail(createUser.getEmail());
		if (!(createUser.getPassword().equals(createUser.getConfirmPassword())) || !(findUser == null)) {
			return ResponseEntity.badRequest().build();
		}
		user.setEmail(createUser.getEmail());
		user.setPassword(encoder.encode(createUser.getPassword()));
		userrepository.save(user);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/login")
	public ResponseEntity<User> login(@RequestBody Credential cred) {
		boolean loginResponse = securityService.login(cred.getEmail(), cred.getPassword());
		if (loginResponse)
			return ResponseEntity.ok().build();
		return ResponseEntity.badRequest().build();
	}

	@PostMapping("/changePassword")
	public ResponseEntity<User> changePassword(@Valid @RequestBody Credential cred, BindingResult bindingResult) {
		User user = userrepository.findByEmail(cred.getEmail());
		if (user == null || bindingResult.hasErrors())
			return ResponseEntity.badRequest().build();
		user.setPassword(encoder.encode(cred.getPassword()));
		userrepository.save(user);
		return ResponseEntity.ok().build();
	}

}
