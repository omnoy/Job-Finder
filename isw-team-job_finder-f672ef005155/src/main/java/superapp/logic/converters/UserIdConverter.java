package superapp.logic.converters;

import superapp.boundaries.sub_boundaries.UserIdBoundary;
import superapp.data.UserIdEntity;

public class UserIdConverter {
	
	public UserIdEntity userIdBoundaryToUserIdEntity(UserIdBoundary userIdBoundary) {
		// Create new UserId Entity
		UserIdEntity idEntity = new UserIdEntity();
		idEntity.setEmail(userIdBoundary.getEmail());
		idEntity.setSuperapp(userIdBoundary.getSuperapp());
		
		return idEntity;
	}
	
	public UserIdBoundary userIdEntityToUserIdBoundary(UserIdEntity userIdEntity) {
		// Create new UserIdBoundary object
		UserIdBoundary userIdBoundary = new UserIdBoundary();
		
		// Set new object attributes
		userIdBoundary.setSuperapp(userIdEntity.getSuperapp());
		userIdBoundary.setEmail(userIdEntity.getEmail());
		
		return userIdBoundary;
	}

}
