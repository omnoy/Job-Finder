package superapp.boundaries;

import jakarta.persistence.EnumType;

import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import superapp.boundaries.sub_boundaries.UserIdBoundary;
import superapp.data.UserRole;

/**
 * The class {@code UserBoundary} is a class which represents a boundary for
 * any user in the project. The class follows the Java Beans rules so it could
 * be marshaled and unmarshaled.
 * 
 * <p>
 * This boundary is composed of:
 * <ul>
 * 	<li>A UserIdBoundary representing the user's identification</li>
 * 	<li>A String representing the user's role</li>
 * 	<li>A String representing the user's username</li>
 * 	<li>A String representing the user's avatar</li>
 * </ul>
 * </p>
 * 
 * @author 	Rom Gat
 * 			Omer Noy
 * 			Noy Hanan
 */
public class UserBoundary {
	
	private UserIdBoundary userId;
	
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	@NotBlank(message = "Username cannot be an empty string")
	private String username;
	
	@NotBlank(message = "Avatar cannot be an empty string")
	private String avatar;
	
	
	public UserBoundary() {
		//do nothing
	}

	public UserIdBoundary getUserId() {
		return userId;
	}

	public void setUserId(UserIdBoundary userId) {
		this.userId = userId;
	}
	
	public UserRole getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = UserRole.valueOf(role);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return "UserBoundary [userId=" + userId + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}
}
