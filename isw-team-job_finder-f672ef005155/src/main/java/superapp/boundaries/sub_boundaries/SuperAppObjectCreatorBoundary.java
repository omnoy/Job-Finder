package superapp.boundaries.sub_boundaries;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * The class {@code ObjectCreatorBoundary} is a class which represents a boundary
 * for identifying the user which created some object in the project. The class
 * follows the Java Beans rules so it could be marshaled and unmarshaled.
 * 
 * <p>
 * This identification is done via <b>the user's identification</b>.
 * </p>
 * 
 * @author 	Rom Gat
 */
public class SuperAppObjectCreatorBoundary {
	@Valid
	@NotNull(message = "UserId of CreatedBy cannot be null")
	private UserIdBoundary userId;
	
	
	public SuperAppObjectCreatorBoundary() {
	}

	public UserIdBoundary getUserId() {
		return userId;
	}

	public void setUserId(UserIdBoundary userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "ObjectCreator [userId=" + userId + "]";
	}
}
