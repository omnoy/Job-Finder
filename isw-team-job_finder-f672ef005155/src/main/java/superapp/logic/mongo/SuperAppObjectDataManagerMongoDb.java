 package superapp.logic.mongo;

import java.util.HashMap;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.sub_boundaries.LocationBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;
import superapp.data.SuperAppObjectEntity;
import superapp.data.SuperAppObjectIdEntity;
import superapp.data.UserIdEntity;
import superapp.data.UserRole;
import superapp.data.crud.SuperAppObjectCrud;
import superapp.logic.HTTPForbiddenException;
import superapp.logic.HTTPNotFoundException;
import superapp.logic.DeprecatedOperationException;
import superapp.logic.HTTPBadRequestException;
import superapp.logic.ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval;
import superapp.logic.components.UserAuthenticationComponent;
import superapp.logic.converters.*;

@Service
public class SuperAppObjectDataManagerMongoDb implements ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval{
	private SuperAppObjectCrud databaseObjectCrud;
	private SuperAppObjectConverter superAppObjectConverter;
	private SuperAppObjectIdConverter superAppObjectIdConverter;
	private String springApplicationName;
	private UserAuthenticationComponent userAuthentication;
	
	public SuperAppObjectDataManagerMongoDb(SuperAppObjectCrud databaseObjectsCrud,
			UserAuthenticationComponent userAuthentication) {
		this.databaseObjectCrud = databaseObjectsCrud;
		this.userAuthentication = userAuthentication;
	}
	
	@Value("${spring.application.name}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}
	
	@PostConstruct
	public void init() {
		this.superAppObjectConverter = new SuperAppObjectConverter();
		this.superAppObjectIdConverter = new SuperAppObjectIdConverter();
	}
	
	public SuperAppObjectBoundary validateSuperAppObjectBoundary(SuperAppObjectBoundary object){
		//alias, type, and createdby are automatically validated
		
		//set default value for active
		if(object.getActive() == null) {
			object.setActive(false);
		}
		
		//set default values for location
		if(object.getLocation() == null ) {
			object.setLocation(new LocationBoundary());
			object.getLocation().setLat(0.0);
			object.getLocation().setLng(0.0);
		}
		else {
			if(object.getLocation().getLat() == null) {
				object.getLocation().setLat(0.0);
			}
			if(object.getLocation().getLng() == null) {
				object.getLocation().setLng(0.0);
			}	
		}

		if(object.getObjectDetails() == null) {
			object.setObjectDetails(new HashMap<String, Object>());
		}

		SuperAppObjectIdBoundary objectId = new SuperAppObjectIdBoundary();
		objectId.setSuperapp(springApplicationName);
		objectId.setInternalObjectId(UUID.randomUUID().toString());
		
		object.setObjectId(objectId);
		object.setCreationTimestamp(new java.util.Date());
		
		return object;
	}
	
	/////////////////////////////////////////////
	//		BASIC OBJECT API
	/////////////////////////////////////////////
	
	@Override
	public SuperAppObjectBoundary createObject(SuperAppObjectBoundary object) {
		object = validateSuperAppObjectBoundary(object);
		SuperAppObjectEntity objectEntity = this.superAppObjectConverter.superAppObjectBoundaryToSuperAppObjectEntity(object);
		
		//validate that only SUPERAPP_USERs can create SuperAppObjects
		UserIdEntity creatorId = objectEntity.getCreatedBy();
		
		if(userAuthentication.getUserRoleNameById(creatorId.getSuperapp(), creatorId.getEmail()) != UserRole.SUPERAPP_USER.toString())
			throw new HTTPForbiddenException("Only SUPERAPP_USERS can create SuperAppObjects.");
		
		//save to database
		this.databaseObjectCrud.save(objectEntity);
		
		///////////////////// IDO ADDED START /////////////////////
//		ResumeBuildCommand rbc = new ResumeBuildCommand();
//		String res = rbc.execute();
//		System.err.println(res);
		///////////////////// IDO ADDED END /////////////////////
		
		return object;
	}

	@Override
	@Deprecated
	public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update) {
		throw new DeprecatedOperationException("This function has been deprecated, please use the alternative updateObject");
	}

	@Override
	@Deprecated
	public SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId) {
		throw new DeprecatedOperationException("This function has been deprecated, please use the alternative getSpecificObject");
	}

	@Override
	@Deprecated
	public List<SuperAppObjectBoundary> getAllObjects() {
		throw new DeprecatedOperationException("This function has been deprecated, please use the alternative getAllObjects");
	}

	@Override
	public void deleteAllObjects() {
		this.databaseObjectCrud.deleteAll();
	}
	
	
	/////////////////////////////////////////////
	//		RELATIONAL API
	/////////////////////////////////////////////
	
	/** This is a helper-method which returns the entity based on a boundary's id.
	 * 
	 * @param	boundaryId
	 * 			The identification of the boundary.
	 * 
	 * @return	The appropriate entity.
	 */
	public Optional<SuperAppObjectEntity> getEntityByIdBoundary(SuperAppObjectIdBoundary boundaryId) {
		return this.databaseObjectCrud.findById(this.superAppObjectIdConverter
				.superAppObjectIdBoundaryToSuperAppObjectIdEntity(boundaryId));
	}

	@Override
	@Deprecated
	public void addChild(SuperAppObjectIdBoundary parentId, SuperAppObjectIdBoundary childId) {
		throw new DeprecatedOperationException("This function has been deprecated, please use the alternative addChild");
	}

	@Override
	@Deprecated
	public List<SuperAppObjectBoundary> getChildren(SuperAppObjectIdBoundary parentId) {
		throw new DeprecatedOperationException("This function has been deprecated, please use the alternative getChildren");
	}

	@Override
	@Deprecated
	public List<SuperAppObjectBoundary> getParents(SuperAppObjectIdBoundary childId) {
		throw new DeprecatedOperationException("This function has been deprecated, please use the alternative getChildren");
	}
	
	
	/////////////////////////////////////////////
	//		ADVANCED RETRIEVAL API
	/////////////////////////////////////////////
	
	/** This is a helper-method which tells whether to include inactive objects in the get methods,
	 * based on the user's role name. If role name is ADMIN - throws 403 - Forbidden.
	 * 
	 * @param	roleName
	 * 			The user's role name.
	 * 
	 * @return	Boolean indicating whether to include the inactive objects for the get requests.
	 * 
	 * @note	Since this is a helper-method for internal use, and it's input comes from within the service,
	 * 			if it receives an invalid role it means an internal error has occurred.
	 */
	public boolean doesIncludeInactive(String roleName) {
		switch (roleName) {
		case "ADMIN":
			throw new HTTPForbiddenException("User doesn't have an appropriate role (Role=" + roleName + ")");
		case "SUPERAPP_USER":
			return true;
		case "MINIAPP_USER":
			return false;
		default:
			throw new RuntimeException("Received user has invalid role (INTERNAL)");
		}
	}
	
	@Override
	public List<SuperAppObjectBoundary> getObjectsByType(String type, String userSuperapp, String userEmail, int size,
			int page) {
		boolean includeInactives = doesIncludeInactive(userAuthentication
				.getUserRoleNameById(userSuperapp, userEmail));
		
		if (includeInactives) {
			return this.databaseObjectCrud
					.findAllByType(type, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
		else {
			return this.databaseObjectCrud
					.findAllByTypeAndActiveIsTrue(type, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
	}

	@Override
	public List<SuperAppObjectBoundary> getObjectsByAlias(String alias, String userSuperapp, String userEmail, int size,
			int page) {
		boolean includeInactives = doesIncludeInactive(userAuthentication
				.getUserRoleNameById(userSuperapp, userEmail));
		
		if (includeInactives) {
			return this.databaseObjectCrud
					.findAllByAlias(alias, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
		else {
			return this.databaseObjectCrud
					.findAllByAliasAndActiveIsTrue(alias, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
	}

	@Override
	public List<SuperAppObjectBoundary> getObjectsByRadiusAroundLocation(double lat, double lng, double distance,
			String distanceUnits, String userSuperapp, String userEmail, int size, int page) {
		boolean includeInactives = doesIncludeInactive(userAuthentication
				.getUserRoleNameById(userSuperapp, userEmail));
		
		Metric metric = null;
		switch(distanceUnits) {
		case "KILOMETERS":
			metric = Metrics.KILOMETERS;
			break;
		case "MILES":
			metric = Metrics.MILES;
			break;
		case "NEUTRAL":
			metric = Metrics.NEUTRAL;
			break;
		default:
			throw new HTTPBadRequestException("");
		}
		
		Point point = new Point(lng, lat);
		Distance dist = new Distance(distance, metric);
		
		if (includeInactives) {
			return this.databaseObjectCrud
					.findAllByLocationNear(point, dist, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
		else {
			return this.databaseObjectCrud
					.findAllByLocationNearAndActiveIsTrue(point, dist, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
	}

	/////////////////////////////////////////////
	//		UPDATED OBJECT API
	/////////////////////////////////////////////
	
	@Override
	public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId,
			SuperAppObjectBoundary update, String userSuperapp, String userEmail) {
		//check permissions
		if(userAuthentication.getUserRoleNameById(userSuperapp, userEmail) != UserRole.SUPERAPP_USER.toString())
			throw new HTTPForbiddenException("Only SUPERAPP_USERS can create SuperAppObjects.");
		
		SuperAppObjectIdEntity objectIdEntity = new SuperAppObjectIdEntity();
		objectIdEntity.setSuperapp(objectSuperApp);
		objectIdEntity.setInternalObjectId(internalObjectId);
		
		SuperAppObjectEntity existingObjectEntity = 
				this.databaseObjectCrud
				.findById(objectIdEntity)
				.orElseThrow(() -> new HTTPNotFoundException("Object {id=" + internalObjectId + "} not found in database."));
	
		if(update.getType() != null) {
			if (update.getType().isBlank()) {
				throw new HTTPBadRequestException();
			}
			existingObjectEntity.setType(update.getType());
		}
		
		if(update.getAlias() != null) {
			if (update.getAlias().isBlank()) {
				throw new HTTPBadRequestException();
			}
			existingObjectEntity.setAlias(update.getAlias());
		}
		
		if(update.getActive() != null) {
			existingObjectEntity.setActive(update.getActive());
		}
		
		if (update.getLocation() != null) {
			if(update.getLocation().getLat() != null) {
				existingObjectEntity.getLocation()[1] = update.getLocation().getLat();
			}
			
			if(update.getLocation().getLng() != null) {
				existingObjectEntity.getLocation()[0] = update.getLocation().getLng();
			}
		}
		
		if(update.getObjectDetails() != null) {
			existingObjectEntity.setObjectDetails(update.getObjectDetails());
		}

		this.databaseObjectCrud.save(existingObjectEntity);
		
		return this.superAppObjectConverter.superAppObjectEntityToSuperAppObjectBoundary(existingObjectEntity);
	}

	@Override
	public SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId, String userSuperapp,
			String userEmail) {
		//check permissions
		boolean includeInactives = doesIncludeInactive(userAuthentication
				.getUserRoleNameById(userSuperapp, userEmail));
		
		SuperAppObjectIdEntity objectIdEntity = new SuperAppObjectIdEntity();
		objectIdEntity.setSuperapp(objectSuperApp);
		objectIdEntity.setInternalObjectId(internalObjectId);
		
		//get object from database
		SuperAppObjectBoundary foundObject = this.databaseObjectCrud.findById(objectIdEntity)
				.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
				.orElseThrow(() -> new HTTPNotFoundException("Object {id=" + internalObjectId + "} not found in database."));
		
		if(includeInactives || foundObject.getActive())
			return foundObject;
		else
			throw new HTTPNotFoundException("Object {id=" + internalObjectId + "} not found in database.");
	}

	@Override
	public List<SuperAppObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page) {
		//check permissions
		boolean includeInactives = doesIncludeInactive(userAuthentication
				.getUserRoleNameById(userSuperapp, userEmail));

		if(includeInactives)
			return this.databaseObjectCrud
					.findAll(PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		else
			return this.databaseObjectCrud
					.findAllByActiveIsTrue(PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
	}

	@Override
	public void addChild(SuperAppObjectIdBoundary parentId, SuperAppObjectIdBoundary childId, String userSuperapp,
			String userEmail) {
		//check permissions
		if(userAuthentication.getUserRoleNameById(userSuperapp, userEmail) != UserRole.SUPERAPP_USER.toString())
			throw new HTTPForbiddenException("Only SUPERAPP_USERS can add children to objects.");
		
		// Get parent entity from DAL:
		SuperAppObjectEntity parent = getEntityByIdBoundary(parentId)
				.orElseThrow(()->new HTTPNotFoundException("Parent object not found by supplied id: " + parentId.toString()));
		
		// Get child entity from DAL:
		SuperAppObjectEntity child = getEntityByIdBoundary(childId)
				.orElseThrow(()->new HTTPNotFoundException("Child object not found by supplied id: " + childId.toString()));
		
		if (!parent.equals(child)) {
			// Connect them:
			parent.addChild(child);
			child.addParent(parent);
			
			// Store in DAL:
			this.databaseObjectCrud.save(child);
			this.databaseObjectCrud.save(parent);
		}
		else { // Same object!
			// Connect the object to itself, both ways:
			parent.addChild(parent);
			parent.addParent(parent);
			
			// Store in DAL:
			this.databaseObjectCrud.save(parent);
		}
	}
	
	@Override
	public List<SuperAppObjectBoundary> getChildren(SuperAppObjectIdBoundary parentId, String userSuperapp,
			String userEmail, int size, int page) {
		//check permissions
		boolean includeInactives = doesIncludeInactive(userAuthentication
				.getUserRoleNameById(userSuperapp, userEmail));
		
		// Get parent entity from DAL:
		SuperAppObjectEntity parent = getEntityByIdBoundary(parentId)
				.orElseThrow(()->new HTTPNotFoundException("Parent object not found by supplied id: " + parentId.toString()));
		
		if(!parent.isActive() && !includeInactives)
			throw new HTTPNotFoundException("Parent object not found by supplied id: " + parentId.toString());
		
		// Get its children:
		if(includeInactives) {
			return this.databaseObjectCrud.findAllByParentsContains(parent, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
		else {
			return this.databaseObjectCrud.findAllByParentsContainsAndActiveIsTrue(parent, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
	}
	
	@Override
	public List<SuperAppObjectBoundary> getParents(SuperAppObjectIdBoundary childId, String userSuperapp,
			String userEmail, int size, int page) {
		boolean includeInactives = doesIncludeInactive(userAuthentication
				.getUserRoleNameById(userSuperapp, userEmail));
		
		// Get child entity from DAL:
		SuperAppObjectEntity child = getEntityByIdBoundary(childId)
				.orElseThrow(()->new HTTPNotFoundException("Child object not found by supplied id: " + childId.toString()));
		
		if(!child.isActive() && !includeInactives)
			throw new HTTPNotFoundException("Child object not found by supplied id: " + childId.toString());
		
		// Get its parents:
		if(includeInactives) {
			return this.databaseObjectCrud.findAllByChildrenContains(child, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
		else {
			return this.databaseObjectCrud.findAllByChildrenContainsAndActiveIsTrue(child, PageRequest.of(page, size, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId")) 
					.stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary)
					.toList();
		}
	}

	
	/////////////////////////////////////////////
	//		UPDATED ADMIN API
	/////////////////////////////////////////////
	
	@Override
	public void deleteAllObjects(String userSuperapp, String userEmail) {
		//check permissions
		if(userAuthentication.getUserRoleNameById(userSuperapp, userEmail) != UserRole.ADMIN.toString())
			throw new HTTPForbiddenException("Only ADMIN can delete all objects.");
		
		this.databaseObjectCrud.deleteAll();
	}
	
	
}
