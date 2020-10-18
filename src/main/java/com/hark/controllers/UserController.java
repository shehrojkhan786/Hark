/**
 * 
 */
package com.hark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author shkhan
 *
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	private SearchAndMatchService searchAndMatchService;
	
//	@PostMapping("/search")
//	public ResponseEntity<?> searchAndMatch(@Valid User user){
//		
//		
//	}
	
}
