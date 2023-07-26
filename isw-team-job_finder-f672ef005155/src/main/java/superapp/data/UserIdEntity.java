package superapp.data;


/**
 * Java Bean class for UserIdEntity.
 * 
 * 
 * @author noyhanan
 *
 */

public class UserIdEntity {
	private String email;
	private String superapp;


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
		return "UserIdEntity [superapp=" + superapp + ", email=" + email + "]";
	}
	
	@Override
	public boolean equals(Object o) {
	    if (o == this)
	        return true;
	    if (!(o instanceof UserIdEntity))
	        return false;
	    
	    UserIdEntity other = (UserIdEntity)o;
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
