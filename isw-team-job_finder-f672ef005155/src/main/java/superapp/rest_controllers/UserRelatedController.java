package superapp.rest_controllers;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import superapp.boundaries.*;
import superapp.boundaries.sub_boundaries.UserIdBoundary;
import superapp.logic.UserService;
import superapp.logic.converters.UserConverter;


/**
 * The class {@code UserRelatedController} is a class which implements the required APIs which
 * are user related, and are not permitted only to administrators.
 * 
 * @author 	Rom Gat
 * 			Omer Noy
 * 			noyhanan
 */
@RestController
@Validated
public class UserRelatedController {
	
	private UserService dataManager;
	private UserConverter userConverter;
	
	/**
	 * This constructor auto 'wires' the database instance to the dataManager.
	 */
	@Autowired
	public UserRelatedController(UserService dataManager, UserConverter userConverter) {
		super();
		this.dataManager = dataManager;
		this.userConverter = userConverter;
	}
	
	/**
	 * This method implements a feature <b>User Sign Up</b> - It takes a NewUserBoundary which represents a
	 * new user, and creates a UserBoundary using it. It also passes the UserBoundary in the lower layers
	 * for storage.
	 * 
	 * @param	newUser
	 * 			The NewUserBoundary representing the new user.
	 */
	@PostMapping(path = "/superapp/users",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createUser (@Valid @RequestBody NewUserBoundary newUser) {
		// Store in database
		UserBoundary user = this.userConverter
				.newUserBoundaryToUserBoundary(newUser);
				
		UserBoundary rv = dataManager.createUser(user);
		
		return rv;
	}
	

	/**
	 * This method implements a feature <b>User Login</b> - It takes the email and superapp name as
	 * identification of the user and returns the appropriate UserBoundary by retrieving it from the layers
	 * below.
	 * 
	 * @param	superapp
	 * 			The superapp's name for identification.
	 * @param	email
	 * 			The user's email for identification.
	 */
	@GetMapping(
		path = {"/superapp/users/login/{superapp}/{email}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary loginUser (
			@PathVariable("superapp") String superapp,
			@PathVariable("email") String email) {
		
		// Create UserIdBoundary instance
		UserIdBoundary userId = new UserIdBoundary();
		userId.setSuperapp(superapp);
		userId.setEmail(email);
		
		// Get from database
		UserBoundary rv = dataManager.login(userId.getSuperapp(), userId.getEmail());
		
		return rv;
	}
	
	/**
	 * This method implements a feature <b>Update User Details</b> - It takes the email and superapp name as
	 * identification of the user and a UserBoundary to pull all details to update from. It passes the UserBoundary
	 * to the layers below to commit the update.
	 * 
	 * @param	user
	 * 			The UserBoundary containing the user's new details.
	 * @param	superapp
	 * 			The superapp's name for identification.
	 * @param	userEmail
	 * 			The user's email for identification.
	 */
	@PutMapping(
		path = {"/superapp/users/{superapp}/{userEmail}"},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateUser (
			@RequestBody UserBoundary user, 
			@PathVariable("superapp") String superapp,
			@PathVariable("userEmail") String userEmail) {

		// Update in database
		dataManager.updateUser(superapp, userEmail, user);
	}
}
