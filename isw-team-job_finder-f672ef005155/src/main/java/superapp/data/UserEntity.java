package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * 
 * @author noyhanan
 */
@Document(collection = "USERS")
public class UserEntity {
	@Id UserIdEntity userId;
	private UserRole role;
	private String username;
	private String avatar;
	
	
	public UserIdEntity getUserId() {
		return userId;
	}
	
	public void setUserId(UserIdEntity userId) {
		this.userId = userId;
	}
	
	public UserRole getRole() {
		return role;
	}
	
	public void setRole(UserRole role) {
		this.role = role;
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
		return "UserEntity [userId=" + userId + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}
	
}
