package superapp.boundaries.sub_boundaries;

import jakarta.validation.constraints.NotBlank;

/**
 * The class {@code ObjectIdBoundary} is a class which represents a boundary
 * for identifying any {@code ObjectBoundary} instance. The class follows the
 * Java Beans rules so it could be marshaled and unmarshaled.
 * 
 * <p>
 * The identification of object boundaries in this project has two components:
 * <ul>
 * <li>A string identifier for the project itself, done via {@code superapp}. Any boundaries
 * in the project should have this parameter set to "2023b.ido.ronen".</li>
 * <li>An integer identifier for a specific object by an internal id, done via
 * {@code internalObjectId}.</li>
 * </ul>
 * </p>
 * 
 * @author 	Rom Gat
 */
public class SuperAppObjectIdBoundary {	
	@NotBlank(message = "Invalid Object Id - Superapp not specified.")
	private String superapp;
	
	@NotBlank(message = "Invalid Object Id - Internal Id not specified.")
	private String internalObjectId;
	
	
	public SuperAppObjectIdBoundary() {
	}

	public String getSuperapp() {
		return superapp;
	}
	
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
	public String getInternalObjectId() {
		return internalObjectId;
	}
	
	public void setInternalObjectId(String internalObjectId) {
		this.internalObjectId = internalObjectId;
	}

	@Override
	public String toString() {
		return "ObjectId [superapp=" + superapp + ", internalObjectId=" + internalObjectId + "]";
	}
	
	@Override
	public boolean equals(Object o) {
	    if (o == this)
	        return true;
	    if (!(o instanceof SuperAppObjectIdBoundary))
	        return false;
	    
	    SuperAppObjectIdBoundary other = (SuperAppObjectIdBoundary)o;
	    return this.getSuperapp().equals(other.getSuperapp()) && this.getInternalObjectId().equals(other.getInternalObjectId());
	}
	
	@Override
	public int hashCode() {
		int x = getSuperapp().hashCode();
		int y = getInternalObjectId().hashCode();
		
		// to hash to naturals into one natural, we'll use Cantor's pairing function with n=2.
		return ((x + y + 1) * (x + y)) / 2 + y;
	}
}
