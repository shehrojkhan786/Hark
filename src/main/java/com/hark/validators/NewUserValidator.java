/**
 * 
 */
package com.hark.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.hark.model.User;
import com.hark.repositories.UserRepository;


/**
 * @author shkhan
 *
 */
@Component
public class NewUserValidator implements Validator {

	@Autowired
	private UserRepository userRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User newUser = (User) target;
		if (userRepository.existsByUsername(newUser.getUsername())) {
			errors.rejectValue("username", "new.account.username.already.exists");
		}
		if (userRepository.existsByEmail(newUser.getEmail())) {
			errors.rejectValue("email", "new.account.email.already.exists");
		}
		if (userRepository.existsByPhone(newUser.getPhone())) {
			errors.rejectValue("phone", "new.account.phone.already.exists");
		}
	}

}
