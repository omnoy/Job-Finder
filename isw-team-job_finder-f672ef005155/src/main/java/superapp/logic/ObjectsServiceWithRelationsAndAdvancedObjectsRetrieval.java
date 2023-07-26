package superapp.logic;

import java.util.List;

import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;

/**
 * The interface {@code ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval} extends the
 * {@code ObjectsRelationsService} and adds the abilities to retrieve objects based on their attributes.
 * 
 * @author Rom Gat
 */
public interface ObjectsServiceWithRelationsAndAdvancedObjectsRetrieval extends ObjectsRelationsService {
	
	public List<SuperAppObjectBoundary> getObjectsByType(String type, String userSuperapp, String userEmail, int size, int page);
	public List<SuperAppObjectBoundary> getObjectsByAlias(String alias, String userSuperapp, String userEmail, int size, int page);
	public List<SuperAppObjectBoundary> getObjectsByRadiusAroundLocation(double lat, double lng, double distance, String distanceUnit, 
																		 String userSuperapp, String userEmail, int size, int page);
	public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update, 
			String userSuperapp, String userEmail);
	public SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId, String userSuperapp, String userEmail);
	public List<SuperAppObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page);
	public void addChild(SuperAppObjectIdBoundary parentId, SuperAppObjectIdBoundary childId, String userSuperapp, String userEmail);
	public List<SuperAppObjectBoundary> getChildren(SuperAppObjectIdBoundary parentId, String userSuperapp, String userEmail, int size, int page);
	public List<SuperAppObjectBoundary> getParents(SuperAppObjectIdBoundary childId, String userSuperapp, String userEmail, int size, int page);
	
	public void deleteAllObjects(String userSuperapp, String userEmail);
}
