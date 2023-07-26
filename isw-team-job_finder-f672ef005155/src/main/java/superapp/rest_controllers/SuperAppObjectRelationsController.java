package superapp.rest_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;
import superapp.logic.ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval;

/**
 * The class {@code SuperAppObjectRelationsController} is a class which implements the required APIs which
 * are related to relationship creation between existing objects.
 * 
 * @author 	Rom Gat
 */
@RestController
public class SuperAppObjectRelationsController {
	private ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval dataManager;
	
	@Autowired
	public SuperAppObjectRelationsController(ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval dataManager) {
		super();
		this.dataManager = dataManager;
	}
	
	/**
	 * This method is a support method to create an Object Id Boundary from raw details.
	 * 
	 * @param	superapp
	 * 			The superapp's name for identification.
	 * @param	internalObjectId
	 * 			The object's internal id for identification.
	 */
	public SuperAppObjectIdBoundary getIdBoundary(String superapp, String internalObjectId) {
		SuperAppObjectIdBoundary objectId = new SuperAppObjectIdBoundary();
		objectId.setSuperapp(superapp);
		objectId.setInternalObjectId(internalObjectId);
		
		return objectId;
	}
	
	/**
	 * This method implements a feature <b>Bind Child Object to Parent Object</b> - It takes the superapp name and
	 * the internal object id as identification of the parent object and a SuperAppObjectIdBoundary representing the
	 * identification for the child object. It passes the these parameters to the layer below to commit the update.
	 * 
	 * @param	childId
	 * 			The SuperAppObjectIdBoundary representing the identification for the child object.
	 * @param	superapp
	 * 			The superapp's name for identification.
	 * @param	internalObjectId
	 * 			The parent object's internal id for identification.
	 */
	@RequestMapping(
		path = {"/superapp/objects/{superapp}/{InternalObjectId}/children"},
		method = {RequestMethod.PUT},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void addChild (
			@RequestBody SuperAppObjectIdBoundary childId, 
			@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail) {
		//Create id for parent
		SuperAppObjectIdBoundary parentId = getIdBoundary(superapp, internalObjectId);
		
		//Update in mock layer
		dataManager.addChild(parentId, childId, userSuperapp, userEmail);
	}
	
	/**
	 * This method implements a feature <b>Retrieve Children</b> - It takes the superapp name and the internal 
	 * object id as identification of the parent object and returns an array of its children by retrieving it from
	 * the layers below.
	 * 
	 * @param	superapp
	 * 			The superapp's name for identification.
	 * @param	internalObjectId
	 * 			The parent object's internal id for identification.
	 */
	@RequestMapping(
		path = {"/superapp/objects/{superapp}/{InternalObjectId}/children"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getChildren (
			@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="20") int size,
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		//Create id for parent
		SuperAppObjectIdBoundary parentId = getIdBoundary(superapp, internalObjectId);
		
		//get specific object from mock layer
		SuperAppObjectBoundary[] rv = dataManager
				.getChildren(parentId, userSuperapp, userEmail, size, page)
				.toArray(new SuperAppObjectBoundary[0]);
		//Return the object
		return rv;
	}
	
	/**
	 * This method implements a feature <b>Retrieve Parents</b> - It takes the superapp name and the internal 
	 * object id as identification of the child object and returns an array of its parents by retrieving it from
	 * the layers below.
	 * 
	 * @param	superapp
	 * 			The superapp's name for identification.
	 * @param	internalObjectId
	 * 			The child object's internal id for identification.
	 */
	@RequestMapping(
		path = {"/superapp/objects/{superapp}/{InternalObjectId}/parents"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getParents (
			@PathVariable("superapp") String superapp,
			@PathVariable("InternalObjectId") String internalObjectId,
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="20") int size,
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		//Create id for child
		SuperAppObjectIdBoundary childId = getIdBoundary(superapp, internalObjectId);
		
		//get specific object from mock layer
		SuperAppObjectBoundary[] rv = dataManager
				.getParents(childId, userSuperapp, userEmail, size, page)
				.toArray(new SuperAppObjectBoundary[0]);
		//Return the object
		return rv;
	}
}
