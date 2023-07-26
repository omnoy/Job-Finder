package superapp.logic.converters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import superapp.boundaries.NewUserBoundary;
import superapp.boundaries.UserBoundary;
import superapp.boundaries.sub_boundaries.UserIdBoundary;
import superapp.data.UserEntity;
import superapp.data.UserIdEntity;


@Component
public class UserConverter {
	private UserIdConverter userIdConverter;
	private String mySuperapp;
	
	public UserConverter() {
		this.userIdConverter = new UserIdConverter();
	}
	
	public String getMySuperapp() {
		return mySuperapp;
	}

	@Value("${spring.application.name}")
	public void setMySuperapp(String mySuperapp) {
		this.mySuperapp = mySuperapp;
	}

	public UserBoundary newUserBoundaryToUserBoundary(NewUserBoundary newUser) {
		// Create new UserBoundary
		UserBoundary rv = new UserBoundary();
		
		// Create new UserId Entity
		UserIdBoundary newId = new UserIdBoundary();
		newId.setEmail(newUser.getEmail());
		newId.setSuperapp(this.getMySuperapp());
		
		// Set userBoundary attributes
		rv.setUserId(newId);
		rv.setUsername(newUser.getUsername());
		rv.setRole(newUser.getRole().name());
		rv.setAvatar(newUser.getAvatar());
		
		return rv;
	}
	
	public UserEntity userBoundaryToUserEntity(UserBoundary userBoundary) {
		// convert userIdBoundary to userIdEntity
		UserIdEntity idEntity = this.userIdConverter
				.userIdBoundaryToUserIdEntity(userBoundary.getUserId());
		
		// Create new UserEntity
		UserEntity userEntity = new UserEntity();
		
		// set userEntity attributes according to userBoundary
		userEntity.setUserId(idEntity);
		userEntity.setRole(userBoundary.getRole());
		userEntity.setUsername(userBoundary.getUsername());
		userEntity.setAvatar(userBoundary.getAvatar());
		
		return userEntity;
	}
	

	public UserBoundary userEntityToUserBoundary(UserEntity userEntity) {
		// Create new UserBoundary object
		UserBoundary userBoundary = new UserBoundary();
		
		// Create new UserIdBoundary object
		UserIdBoundary userIdBoundary = this.userIdConverter.userIdEntityToUserIdBoundary
				(userEntity.getUserId());
		
		// Set new objects attributes
		userBoundary.setUserId(userIdBoundary);
		userBoundary.setUsername(userEntity.getUsername());
		userBoundary.setRole(userEntity.getRole().name());
		userBoundary.setAvatar(userEntity.getAvatar());
		
		return userBoundary;
	}

}
