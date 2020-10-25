/**
 * 
 */
package com.hark.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hark.model.Role;
import com.hark.model.User;
import com.hark.model.enums.ERole;
import com.hark.model.payload.request.LoginRequest;
import com.hark.model.payload.request.SignupRequest;
import com.hark.model.payload.response.JwtResponse;
import com.hark.model.payload.response.MessageResponse;
import com.hark.repositories.RoleRepository;
import com.hark.repositories.UserRepository;
import com.hark.securty.utils.JwtUtils;
import com.hark.services.UserDetailsImpl;

/**
 * @author shkhan
 *
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
				userDetails.getEmail(), userDetails.getPhone(), roles.get(0)));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		if (userRepository.existsByPhone(signUpRequest.getPhone())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: phone is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()), signUpRequest.getPhone(),
				signUpRequest.getPoliticalParty(), signUpRequest.getCountry(),signUpRequest.getName());

		String strRole = signUpRequest.getRole();

		final Role userRole;

		if (strRole == null || strRole.isBlank()) {
			userRole = roleRepository.findByName(ERole.USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		} else {
			switch (strRole) {
			case "admin":
				userRole = roleRepository.findByName(ERole.ADMIN)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				break;
			case "mod":
				userRole = roleRepository.findByName(ERole.MODERATOR)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				break;
			default:
				userRole = roleRepository.findByName(ERole.USER)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			}
		}

		user.setRole(userRole);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	@PostMapping("/checkUsername")
	public ResponseEntity<?> checkUsername(@Valid String username) {
		if (userRepository.existsByUsername(username)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}
		return ResponseEntity.ok(new MessageResponse("Username available!!!"));
	}
	
	@PostMapping("/checkEmail")
	public ResponseEntity<?> checkEmail(@Valid String email) {
		if (userRepository.existsByEmail(email)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already present!"));
		}
		return ResponseEntity.ok(new MessageResponse("Email available!!!"));
	}
	
	@PostMapping("/checkPhone")
	public ResponseEntity<?> checkPhone(@Valid Long phone) {
		if (userRepository.existsByPhone(phone)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Phone number is already present!"));
		}
		return ResponseEntity.ok(new MessageResponse("Phone number available!!!"));
	}
	
}
