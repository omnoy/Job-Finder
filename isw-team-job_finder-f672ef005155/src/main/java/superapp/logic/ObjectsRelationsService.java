package superapp.logic;

import java.util.List;

import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;

/**
 * The interface {@code ObjectsRelationsService} extends the {@code ObjectsService} and adds
 * the abilities to create and manage relationships between existing objects.
 * 
 * @author Rom Gat
 */
public interface ObjectsRelationsService extends ObjectsService {
	
	@Deprecated
	public void addChild(SuperAppObjectIdBoundary parentId, SuperAppObjectIdBoundary childId);
	@Deprecated
	public List<SuperAppObjectBoundary> getChildren(SuperAppObjectIdBoundary parentId);
	@Deprecated
	public List<SuperAppObjectBoundary> getParents(SuperAppObjectIdBoundary childId);
	
}
