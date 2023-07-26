package superapp.boundaries;

import org.springframework.validation.annotation.Validated;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


import superapp.data.UserRole;

/**
 * The class {@code NewUserBoundary} is a class which represents a boundary for
 * any new user in the project. The class follows the Java Beans rules so it could
 * be marshaled and unmarshaled.
 * 
 * <p>
 * This boundary is composed of:
 * <ul>
 * 	<li>A String representing the new user's email</li>
 * 	<li>A String representing the new user's role</li>
 * 	<li>A String representing the new user's username</li>
 * 	<li>A String representing the new user's avatar</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This boundary is used when a user wishes to sign up to the application.
 * </p>
 * 
 * @author 	Rom Gat
 * 			Omer Noy
 * 			noyhanan
 */

@Validated
public class NewUserBoundary {
	
	@NotBlank(message = "Email cannot be blank / null")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Username cannot be blank / null")
    private String username;
    
    @NotBlank(message = "Avatar cannot be blank / null")
    private String avatar;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;

    
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
		return "NewUserBoundary [email=" + email + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}
}
