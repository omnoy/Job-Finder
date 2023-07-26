package superapp.logic.mongo;

import java.util.List;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import superapp.boundaries.UserBoundary;
import superapp.data.UserEntity;
import superapp.data.UserIdEntity;
import superapp.data.UserRole;
import superapp.data.crud.UserCrud;
import superapp.logic.DeprecatedOperationException;
import superapp.logic.HTTPBadRequestException;
import superapp.logic.HTTPConflictException;
import superapp.logic.HTTPForbiddenException;
import superapp.logic.HTTPNotFoundException;
import superapp.logic.UpdatedUserService;
import superapp.logic.components.UserAuthenticationComponent;
import superapp.logic.converters.UserConverter;


/**
 * 
 * @author noyhanan
 * 		   Ido Ronen
 *
 */
@Service
public class UserDataManagerMongoDB implements UpdatedUserService {
	private UserCrud databaseUserCrud;
	private UserConverter userConverter;
	private UserAuthenticationComponent userAuthentication;
	
	public UserDataManagerMongoDB (UserCrud databaseUserCrud, UserConverter userConverter,
			UserAuthenticationComponent userAuthentication) {
		this.databaseUserCrud = databaseUserCrud;
		this.userConverter = userConverter;
		this.userAuthentication = userAuthentication;
	}
	
	@Override
	public UserBoundary createUser(UserBoundary user) {		
		// convert userBoundary from RESTAPI request to userEntity
		UserEntity userEntity = this.userConverter.userBoundaryToUserEntity(user);
		
		// Check if user already exists
		Optional<UserEntity> existing = this.databaseUserCrud
				.findById(userEntity.getUserId());
		
		if (!existing.isEmpty()) {
			throw new HTTPConflictException("Invalid User Creation, User already exists");
		}
		
		// Save userEntity to database
		this.databaseUserCrud.save(userEntity);
		return user;
	}
	

	@Override
	public UserBoundary login(String userSuperApp, String userEmail) {
		// Create UserIdEntity to pull from database
		UserIdEntity userIdEntity = new UserIdEntity();
		userIdEntity.setSuperapp(userSuperApp);
		userIdEntity.setEmail(userEmail);
		
		// Get userEntity from database and convert it to userBoundary
		UserEntity existing = this.databaseUserCrud
				.findById(userIdEntity)
				.orElseThrow(() -> new HTTPNotFoundException("User: " + userIdEntity + " not registered"));
		
		// convert existing from UserEntity to UserBoundary
		UserBoundary userBoundary = this.userConverter.userEntityToUserBoundary(existing);
		
		return userBoundary;
	}
	

	@Override
	public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update) {
		
		// Create UserIdEntity to pull from database
		UserIdEntity userIdEntity = new UserIdEntity();
		userIdEntity.setSuperapp(userSuperApp);
		userIdEntity.setEmail(userEmail);
		
		// Search for userIdEntity in database
		UserEntity existing = this.databaseUserCrud
				.findById(userIdEntity)
				.orElseThrow(() -> new HTTPNotFoundException("User: " + userIdEntity + " not registered"));
		
		// check if information was updated
		if (update.getUsername() != null) {
			if (update.getUsername().isBlank()) {
				throw new HTTPBadRequestException("Cannot update user with blank username.");
			}
			existing.setUsername(update.getUsername());
		}
		
		if (update.getRole()!= null) {
			existing.setRole(update.getRole());
		}
		
		if (update.getAvatar() != null) {
			if (update.getAvatar().isBlank()) {
				throw new HTTPBadRequestException("Cannot update user with blank avatar.");
			}
			existing.setAvatar(update.getAvatar());
		}
		
		this.databaseUserCrud.save(existing);
		
		return this.userConverter.userEntityToUserBoundary(existing);
	}

	
	@Override
	@Deprecated
	public List<UserBoundary> getAllUsers() {
		// Map all saved UserEntity objects to list of UserBoundary objects
		return this.databaseUserCrud
			.findAll()
			.stream() // Stream<UserEntity>
			.map(this.userConverter::userEntityToUserBoundary) // Stream<UserBoudary>
			.toList(); // List<Message>
	}
	

	
	@Override
	@Deprecated
	public void deleteAllUsers() {
		throw new DeprecatedOperationException("This function has been deprecated, please use the alternative deleteAllUsers");
	}

	
	/////////////////////////////////////////////
	//		UPDATED ADMIN API
	/////////////////////////////////////////////
	
	
	@Override
	public void deleteAllUsers(String userSuperapp, String userEmail) {
		//check permissions
		if(userAuthentication.getUserRoleNameById(userSuperapp, userEmail) != UserRole.ADMIN.toString())
			throw new HTTPForbiddenException("Only ADMIN can delete all users.");
		
		this.databaseUserCrud.deleteAll();
	}
	
	@Override
	public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page) {
		//check permissions
		if(userAuthentication.getUserRoleNameById(userSuperapp, userEmail) != UserRole.ADMIN.toString())
			throw new HTTPForbiddenException("Only ADMIN can get all users.");
		
		// Map all saved UserEntity objects to list of UserBoundary objects
		return this.databaseUserCrud
			.findAll(PageRequest.of(page, size, Direction.ASC, "userId.email"))
			.stream() // Stream<UserEntity>
			.map(this.userConverter::userEntityToUserBoundary) // Stream<UserBoudary>
			.toList(); // List<Message>
	}
	
}
