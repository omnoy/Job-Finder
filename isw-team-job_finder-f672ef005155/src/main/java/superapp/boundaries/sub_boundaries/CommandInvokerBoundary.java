package superapp.boundaries.sub_boundaries;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * The class {@code CommandInvokerBoundary} is a class which represents a boundary
 * for identifying the user which invoked some command in the project. The class
 * follows the Java Beans rules so it could be marshaled and unmarshaled.
 * 
 * <p>
 * This identification is done via <b>the user's identification</b>.
 * </p>
 * 
 * @author 	Rom Gat
 */
public class CommandInvokerBoundary {
	@Valid
	@NotNull(message = "Invalid command - Command invoker not specified")
	private UserIdBoundary userId;
	
	public CommandInvokerBoundary() {
	}

	public UserIdBoundary getUserId() {
		return userId;
	}
	
	public void setUserId(UserIdBoundary userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "CommandInvokerBoundary [userId=" + userId + "]";
	}
}
