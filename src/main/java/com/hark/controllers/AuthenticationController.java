/**
 *
 */
package com.hark.controllers;

import com.hark.model.Role;
import com.hark.model.User;
import com.hark.model.enums.ERole;
import com.hark.model.enums.ResponseStatus;
import com.hark.model.payload.request.LoginRequest;
import com.hark.model.payload.request.SignupRequest;
import com.hark.model.payload.response.JwtResponse;
import com.hark.model.payload.response.MessageResponse;
import com.hark.repositories.RoleRepository;
import com.hark.repositories.UserRepository;
import com.hark.securty.utils.JwtUtils;
import com.hark.services.impl.UserDetailsImpl;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private JavaMailSender mailSender;

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
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) {
        MessageResponse response = new MessageResponse();
//		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
//		}

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            response.setMessage("Error: Email is already in use!");
            response.setStatus(ResponseStatus.ERROR.name());
            return ResponseEntity.ok(response);
        }

//		if (userRepository.existsByPhone(signUpRequest.getPhone())) {
//			return ResponseEntity.badRequest().body(new MessageResponse("Error: phone is already in use!"));
//		}

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

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
        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);
        userRepository.save(user);

        String siteURL = request.getRequestURL().toString();
        siteURL = siteURL.replace(request.getServletPath(), "");
        try {
            sendVerificationEmail(user, siteURL);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setMessage("User registered successfully!");
        response.setStatus(ResponseStatus.SUCCESS.name());
        return ResponseEntity.ok(response);
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "sheheroj.khan@jellylogic.in";
        String senderName = "Hark";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "<br/>Put this otp [[code]] in app."
                + "Thank you,<br>"
                + "Your company name.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());
        content = content.replace("[[code]]", user.getVerificationCode());
        String verifyURL = siteURL + "/api/auth/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody String code) {
        MessageResponse response = new MessageResponse();
        User user = userRepository.findByVerificationCode(code);
        if (user == null || user.isEnabled()) {
			response.setMessage("Error: Email is already in use!");
			response.setStatus(ResponseStatus.FAILED.name());
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);
			response.setMessage("Error: Email is already in use!");
			response.setStatus(ResponseStatus.SUCCESS.name());
        }
		return ResponseEntity.ok(response);
    }

    @PostMapping("/checkUsername")
    public ResponseEntity<?> checkUsername(@Valid @RequestBody String username) {
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        return ResponseEntity.ok(new MessageResponse("Username available!!!"));
    }

    @PostMapping("/checkEmail")
    public ResponseEntity<?> checkEmail(@Valid @RequestBody String email) {
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already present!"));
        }
        return ResponseEntity.ok(new MessageResponse("Email available!!!"));
    }

    @PostMapping("/checkPhone")
    public ResponseEntity<?> checkPhone(@Valid @RequestBody Long phone) {
        if (userRepository.existsByPhone(phone)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Phone number is already present!"));
        }
        return ResponseEntity.ok(new MessageResponse("Phone number available!!!"));
    }

}
