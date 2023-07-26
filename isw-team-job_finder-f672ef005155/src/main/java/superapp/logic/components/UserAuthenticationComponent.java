package superapp.logic.components;

import org.springframework.stereotype.Component;

import superapp.data.UserIdEntity;
import superapp.data.crud.UserCrud;
import superapp.logic.HTTPForbiddenException;

@Component
public class UserAuthenticationComponent {
	private UserCrud databaseUserCrud;
	
	public UserAuthenticationComponent(UserCrud databaseUserCrud) {
		this.databaseUserCrud = databaseUserCrud;
	}
	
	/** This is a helper-method which returns a user's role name by his id (If found, else throws 403 - Forbidden).
	 * 
	 * @param	userSuperapp
	 * 			The user's superapp name.
	 * @param 	userEmail
	 * 			The user's email.
	 * 
	 * @return	The user entity.
	 */
	public String getUserRoleNameById(String userSuperapp, String userEmail) {
		// Create user id entity.
		UserIdEntity userIdEntity = new UserIdEntity();
		userIdEntity.setSuperapp(userSuperapp);
		userIdEntity.setEmail(userEmail);
		
		// Return userEntity from database
		return this.databaseUserCrud
				.findById(userIdEntity)
				.orElseThrow(() -> new HTTPForbiddenException("User: " + userIdEntity + " not registered"))
				.getRole()
				.toString();
	}
	
}
