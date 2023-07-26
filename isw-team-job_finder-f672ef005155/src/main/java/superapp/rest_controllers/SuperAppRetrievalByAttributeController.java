package superapp.rest_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import superapp.boundaries.SuperAppObjectBoundary;
import superapp.logic.ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval;

/**
 * The class {@code SuperAppRetrievalByAttributeController} is a responsible for all APIs related to
 * object retrieval by attribute. More specifically, this controller includes methods for:
 * <ul>
 * 	<li>Retrieving all object with specific {@code type}</li>
 * 	<li>Retrieving all object with specific {@code alias}</li>
 * 	<li>Retrieving all object in a radius around {@code location}</li>
 * </ul>
 * 
 * @author 	Rom Gat
 */
@RestController
public class SuperAppRetrievalByAttributeController {
	private ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval dataManager;
	
	@Autowired
	public SuperAppRetrievalByAttributeController(ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval dataManager) {
		super();
		this.dataManager = dataManager;
	}
	
	/**
	 * This method implements a feature <b>Retrieve Objects by Type</b> - It takes a possible
	 * type for an object, as well as user identification and pagination details, and returns a page
	 * of objects with that type, filtered by activity if the requesting user has the MINIAPP_USER role.
	 * 
	 * @param	type
	 * 			The object type to search by.
	 * @param	userSuperapp
	 * 			The user's superapp name for identification.
	 * @param	userEmail
	 * 			The user's email for identification.
	 * @param	size
	 * 			Page size.
	 * @param	page
	 * 			Page index.
	 */
	@RequestMapping(
		path = {"/superapp/objects/search/byType/{type}"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getObjectsByType(
			@PathVariable("type") String type,
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="20") int size,
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		//get page of objects from BL layer
		SuperAppObjectBoundary[] rv = dataManager
				.getObjectsByType(type, userSuperapp, userEmail, size, page)
				.toArray(new SuperAppObjectBoundary[0]);
		
		//Return the object
		return rv;
	}
	
	/**
	 * This method implements a feature <b>Retrieve Objects by Alias</b> - It takes a possible
	 * alias for an object, as well as user identification and pagination details, and returns a page
	 * of objects with that type, filtered by activity if the requesting user has the MINIAPP_USER role.
	 * 
	 * @param	alias
	 * 			The object alias to search by.
	 * @param	userSuperapp
	 * 			The user's superapp name for identification.
	 * @param	userEmail
	 * 			The user's email for identification.
	 * @param	size
	 * 			Page size.
	 * @param	page
	 * 			Page index.
	 */
	@RequestMapping(
		path = {"/superapp/objects/search/byAlias/{alias}"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getObjectsByAlias(
			@PathVariable("alias") String alias,
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="20") int size,
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		//get page of objects from BL layer
		SuperAppObjectBoundary[] rv = dataManager
				.getObjectsByAlias(alias, userSuperapp, userEmail, size, page)
				.toArray(new SuperAppObjectBoundary[0]);
		
		//Return the object
		return rv;
	}
	
	/**
	 * This method implements a feature <b>Retrieve Objects by Location</b> - It takes a possible
	 * location of an object in the world, a distance with some unit to represent a radius around the location,
	 * as well as user identification and pagination details, and returns a page of objects with that type,
	 * filtered by activity if the requesting user has the MINIAPP_USER role.
	 * 
	 * @param	lat
	 * 			The latitude to search by.
	 * @param	lng
	 * 			The longitude to search by.
	 * @param	distance
	 * 			The radius around the location to search by.
	 * @param	distanceUnits
	 * 			The units of distance.
	 * @param	userSuperapp
	 * 			The user's superapp name for identification.
	 * @param	userEmail
	 * 			The user's email for identification.
	 * @param	size
	 * 			Page size.
	 * @param	page
	 * 			Page index.
	 */
	@RequestMapping(
		path = {"/superapp/objects/search/byLocation/{lat}/{lng}/{distance}"},
		method = {RequestMethod.GET},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public SuperAppObjectBoundary[] getObjectsByRadiusAroundLocation(
			@PathVariable("lat") double lat,
			@PathVariable("lng") double lng,
			@PathVariable("distance") double distance,
			@RequestParam(name="distanceUnits", required=false, defaultValue="NEUTRAL") String distanceUnits,
			@RequestParam(name="userSuperapp", required=true) String userSuperapp,
			@RequestParam(name="userEmail", required=true) String userEmail,
			@RequestParam(name="size", required=false, defaultValue="20") int size,
			@RequestParam(name="page", required=false, defaultValue="0") int page) {
		//get page of objects from BL layer
		SuperAppObjectBoundary[] rv = dataManager
				.getObjectsByRadiusAroundLocation(lat, lng, distance, distanceUnits, userSuperapp, userEmail, size, page)
				.toArray(new SuperAppObjectBoundary[0]);
		
		//Return the object
		return rv;
	}
	
}
