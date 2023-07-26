package superapp.rest_controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import superapp.boundaries.SuperAppObjectBoundary;
import superapp.logic.ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval;

/**
 * The class {@code SuperAppObjectsController} is a class which implements the required APIs which
 * are object related, and are not permitted only to administrators.
 * 
 * @author 	Omer Noy
 * 			Rom Gat
 */
@RestController
@Validated
public class SuperAppObjectsController {
	private ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval dataManager;
	
	@Autowired
	public SuperAppObjectsController(ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval dataManager) {
		super();
		this.dataManager = dataManager;
	}
	
	/**
	 * This method implements an feature <b>Create Object</b> - It takes an ObjectBoundary
	 * without an id, adds an id to it and returns it. It also passes the ObjectBoundary in the lower
	 * layers for storage.
	 * 
	 * @param	newObject
	 * 			The ObjectBoundary representing the new object.
	 */
	@RequestMapping(
		path = {"/superapp/objects"},
		method = {RequestMethod.POST},
		produces = {MediaType.APPLICATION_JSON_VALUE},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary createObject (@Valid @RequestBody SuperAppObjectBoundary newObject) {
		
		// Store in data layer.
		SuperAppObjectBoundary rv = dataManager.createObject(newObject);
		
		//Return generated object
		return rv;
	}
	
	/**
	 * This method implements a feature <b>Update Object Details</b> - It takes the superapp name and the internal
	 * object id as identification of the object and an ObjectBoundary to pull all details to update from. It passes
	 * the Object to the layers below to commit the update.
	 * 
	 * @param	object
	 * 			The ObjectBoundary containing the user's new details.
	 * @param	superapp
	 * 			The superapp's name for identification.
	 * @param	internalObjectId
	 * 			The object's internal id for identification.
	 */
	@RequestMapping(
		path = {"/superapp/objects/{superapp}/{InternalObjectId}"},
		method = {RequestMethod.PUT},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject (
			@RequestBody SuperAppObjectBoundary object, 
			@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail) {
		
		//Update in mock layer
		dataManager.updateObject(superapp, internalObjectId, object, userSuperapp, userEmail);
	}
	
	/**
	 * This method implements a feature <b>Retrieve Object</b> - It takes the superapp name and the internal 
	 * object id as identification of the object and returns the appropriate ObjectBoundary by retrieving it from
	 * the layers below.
	 * 
	 * @param	superapp
	 * 			The superapp's name for identification.
	 * @param	internalObjectId
	 * 			The object's internal id for identification.
	 */
	@RequestMapping(
		path = {"/superapp/objects/{superapp}/{InternalObjectId}"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary retrieveObject (
			@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail) {
		
		//get specific object from mock layer
		SuperAppObjectBoundary rv = dataManager.getSpecificObject(superapp, internalObjectId, userSuperapp, userEmail);
		//Return the object
		return rv;
	}
	
	/**
	 * This method implements a feature <b>Retrieve All Objects</b> - It returns an array of all existing
	 * ObjectBoundaries by retrieving it from the layers below.
	 */
	@RequestMapping(
		path = {"/superapp/objects"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] retrieveAllObjects (
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="20") int size,
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		// Get all objects from mock layer.
		List<SuperAppObjectBoundary> rv = dataManager.getAllObjects(userSuperapp, userEmail, size, page);
		return rv.toArray(new SuperAppObjectBoundary[rv.size()]);
	}
}
