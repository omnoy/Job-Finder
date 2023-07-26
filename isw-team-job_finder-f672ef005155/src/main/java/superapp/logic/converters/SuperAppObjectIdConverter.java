package superapp.logic.converters;

import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;
import superapp.data.SuperAppObjectIdEntity;

public class SuperAppObjectIdConverter {
	public SuperAppObjectIdEntity superAppObjectIdBoundaryToSuperAppObjectIdEntity(SuperAppObjectIdBoundary idBoundary) {
		//Create new entity
		SuperAppObjectIdEntity idEntity = new SuperAppObjectIdEntity();
		
		//Set fields
		idEntity.setSuperapp(idBoundary.getSuperapp());
		idEntity.setInternalObjectId(idBoundary.getInternalObjectId());
		
		return idEntity;
	}
	
	public SuperAppObjectIdBoundary superAppObjectEntityToSuperAppObjectBoundary(SuperAppObjectIdEntity idEntity) {
		//Create new entity
		SuperAppObjectIdBoundary idBoundary = new SuperAppObjectIdBoundary();
		
		//Set fields
		idBoundary.setSuperapp(idEntity.getSuperapp());
		idBoundary.setInternalObjectId(idEntity.getInteralObjectId());
		
		return idBoundary;
	}
}
