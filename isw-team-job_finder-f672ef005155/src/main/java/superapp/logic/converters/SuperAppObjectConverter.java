package superapp.logic.converters;

import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.sub_boundaries.LocationBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectCreatorBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;
import superapp.boundaries.sub_boundaries.UserIdBoundary;
import superapp.data.SuperAppObjectEntity;
import superapp.data.SuperAppObjectIdEntity;
import superapp.data.UserIdEntity;

public class SuperAppObjectConverter {
	private SuperAppObjectIdConverter superAppObjectIdConverter;
	private UserIdConverter userIdConvertor;
	
	public SuperAppObjectConverter() {
		this.superAppObjectIdConverter = new SuperAppObjectIdConverter();
		this.userIdConvertor = new UserIdConverter();
	}
	
	public SuperAppObjectEntity superAppObjectBoundaryToSuperAppObjectEntity(SuperAppObjectBoundary object) {
		SuperAppObjectEntity objectEntity = new SuperAppObjectEntity();
		
		SuperAppObjectIdEntity idEntity = this.superAppObjectIdConverter
				.superAppObjectIdBoundaryToSuperAppObjectIdEntity(object.getObjectId());
		
		objectEntity.setObjectId(idEntity);
		objectEntity.setType(object.getType());
		objectEntity.setAlias(object.getAlias());
		objectEntity.setActive(object.getActive());
		objectEntity.setCreationTimeStamp(object.getCreationTimestamp());
		
		objectEntity.getLocation()[1] = object.getLocation().getLat();
		objectEntity.getLocation()[0] = object.getLocation().getLng();
		
		UserIdEntity createdBy = this.userIdConvertor.userIdBoundaryToUserIdEntity(
				object.getCreatedBy().getUserId());
		objectEntity.setCreatedBy(createdBy);
		
		objectEntity.setObjectDetails(object.getObjectDetails());
		return objectEntity;
		
	}
	
	public SuperAppObjectBoundary superAppObjectEntityToSuperAppObjectBoundary(SuperAppObjectEntity object) {
		SuperAppObjectBoundary objectBoundary = new SuperAppObjectBoundary();
		
		SuperAppObjectIdBoundary idBoundary = this.superAppObjectIdConverter
				.superAppObjectEntityToSuperAppObjectBoundary(object.getObjectId());
		
		objectBoundary.setObjectId(idBoundary);
		objectBoundary.setType(object.getType());
		objectBoundary.setAlias(object.getAlias());
		objectBoundary.setActive(object.isActive());
		objectBoundary.setCreationTimestamp(object.getCreationTimeStamp());
		
		LocationBoundary location = new LocationBoundary();
		location.setLat(object.getLocation()[1]);
		location.setLng(object.getLocation()[0]);
		objectBoundary.setLocation(location);
		
		UserIdBoundary createdBy = this.userIdConvertor.userIdEntityToUserIdBoundary(object.getCreatedBy());
		SuperAppObjectCreatorBoundary creatorBoundary = new SuperAppObjectCreatorBoundary();
		creatorBoundary.setUserId(createdBy);
		objectBoundary.setCreatedBy(creatorBoundary);
		
		objectBoundary.setObjectDetails(object.getObjectDetails());		
		
		return objectBoundary;
	}
}