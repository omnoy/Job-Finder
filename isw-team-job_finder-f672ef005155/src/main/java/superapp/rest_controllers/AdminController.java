package superapp.rest_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import superapp.boundaries.MiniAppCommandBoundary;
import superapp.boundaries.UserBoundary;
import superapp.logic.MiniAppCommandsServiceWithAsyncSupport;
import superapp.logic.ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval;
import superapp.logic.UpdatedUserService;

/**
 * The class {@code AdminController} is a class which implements all required APIs which
 * are permitted only to administrators.
 * 
 * @author  Rom Gat
 */
@RestController
public class AdminController {
	private UpdatedUserService usersDataManager;
	private ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval objectsDataManager;
	private MiniAppCommandsServiceWithAsyncSupport commandsDataManager;
	
	@Autowired
	public AdminController(UpdatedUserService userDataManager, ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval objectsDataManager,
			MiniAppCommandsServiceWithAsyncSupport commandsDataManager) {
		super();
		this.usersDataManager = userDataManager;
		this.objectsDataManager = objectsDataManager;
		this.commandsDataManager = commandsDataManager;
	}
		
	
	/**
	 * This method implements an feature <b>Delete All Users</b> for testing - It removes all existing
	 * UserBoundaries by removing them from the layers below.
	 */
	@RequestMapping(
		path = {"/superapp/admin/users"},
		method = {RequestMethod.DELETE})
	public void deleteAllUsers (
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail) {
		this.usersDataManager.deleteAllUsers(userSuperapp, userEmail);
	}
	
	
	/**
	 * This method implements an feature <b>Delete All Objects</b> for testing - It removes all existing
	 * ObjectBoundaries by removing them from the layers below.
	 */
	@RequestMapping(
		path = {"/superapp/admin/objects"},
		method = {RequestMethod.DELETE})
	public void deleteAllObjects (
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail) {
		this.objectsDataManager.deleteAllObjects(userSuperapp, userEmail);
	}
	
	/**
	 * This method implements an feature <b>Delete All Commands</b> for testing - It removes all existing
	 * MiniAppCommandBoundary by removing them from the layers below.
	 */
	@RequestMapping(
		path = {"/superapp/admin/miniapp"},
		method = {RequestMethod.DELETE})
	public void deleteAllCommandsHistory (
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail) {
		this.commandsDataManager.deleteAllCommands(userSuperapp, userEmail);
	}
	
	/**
	 * This method implements an feature <b>Retrieve All Users</b> for testing - It returns an array of all
	 * existing UserBoundaries by retrieving it from the layers below.
	 */
	@RequestMapping(
		path = {"/superapp/admin/users"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary[] exportAllUsers (
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="20") int size,
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		return usersDataManager.getAllUsers(userSuperapp, userEmail, size, page).toArray(new UserBoundary[0]);
	}
	
	/**
	 * This method implements an feature <b>Retrieve All Commands</b> for testing - It returns an array of all
	 * existing MiniAppCommandBoundaries by retrieving it from the layers below.
	 */
	@RequestMapping(
		path = {"/superapp/admin/miniapp"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public MiniAppCommandBoundary[] exportAllCommandsHistory (
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="20") int size,
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		return commandsDataManager.getAllCommands(userSuperapp, userEmail, size, page).toArray(new MiniAppCommandBoundary[0]);
	}
	
	/**
	 * This method implements an feature <b>Retrieve All Commands of MiniApp</b> for testing - It returns an array
	 * of all existing MiniAppCommandBoundaries issued by a specific miniapp by retrieving it from the layers below.
	 * 
	 * @param	miniAppName
	 * 			The miniapp's name for identification.
	 */
	@RequestMapping(
		path = {"/superapp/admin/miniapp/{miniAppName}"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public MiniAppCommandBoundary[] exportMiniAppCommandsHistory (
			@PathVariable("miniAppName") String miniAppName,
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="20") int size,
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		return commandsDataManager.getAllMiniAppCommands(miniAppName, userSuperapp, userEmail, size, page).toArray(new MiniAppCommandBoundary[0]);
	}
}
