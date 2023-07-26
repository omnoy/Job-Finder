package superapp.boundaries.sub_boundaries;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * The class {@code UserIdBoundary} is a class which represents a boundary
 * for identifying any {@code UserBoundary} instance. The class follows the
 * Java Beans rules so it could be marshaled and unmarshaled.
 * 
 * <p>
 * The identification of user boundaries in this project has two components:
 * <ul>
 * <li>A string identifier for the project itself, done via {@code superapp}. Any boundaries
 * in the project should have this parameter set to "2023b.ido.ronen".</li>
 * <li>A string identifier for a specific user by email, done via {@code email}.</li>
 * </ul>
 * </p>
 * 
 * @author 	Rom Gat
 */
public class UserIdBoundary {
	@NotBlank(message = "SuperApp cannot be blank / null")
	private String superapp;
	
	@NotBlank(message = "Email cannot be blank / null")
	@Email(message = "Invalid Email format")
	private String email;
	
	
	public UserIdBoundary() {
	}
	
	public String getSuperapp() {
		return superapp;
	}
	
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserId [superapp=" + superapp + ", email=" + email + "]";
	}
	
	@Override
	public boolean equals(Object o) {
	    if (o == this)
	        return true;
	    if (!(o instanceof UserIdBoundary))
	        return false;
	    
	    UserIdBoundary other = (UserIdBoundary)o;
	    return this.getSuperapp().equals(other.getSuperapp()) && this.getEmail().equals(other.getEmail());
	}
	
	@Override
	public int hashCode() {
		int x = getSuperapp().hashCode();
		int y = getEmail().hashCode();
		
		// to hash to naturals into one natural, we'll use Cantor's pairing function with n=2.
		return ((x + y + 1) * (x + y)) / 2 + y;
	}
}
