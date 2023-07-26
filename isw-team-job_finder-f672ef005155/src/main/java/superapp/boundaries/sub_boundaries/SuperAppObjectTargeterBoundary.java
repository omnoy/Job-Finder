package superapp.boundaries.sub_boundaries;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * The class {@code ObjectTargeterBoundary} is a class which represents a boundary
 * for containing an object's identifier for commands in the project. The class
 * follows the Java Beans rules so it could be marshaled and unmarshaled.
 * 
 * <p>
 * This identification is done via <b>the object's identification</b>.
 * </p>
 * 
 * @author 	Rom Gat
 */
public class SuperAppObjectTargeterBoundary {
	
	@Valid
	@NotNull(message = "Invalid command - Target object not specified")
	private SuperAppObjectIdBoundary objectId;
	
	
	public SuperAppObjectTargeterBoundary() {
	}

	public SuperAppObjectIdBoundary getObjectId() {
		return objectId;
	}

	public void setObjectId(SuperAppObjectIdBoundary objectId) {
		this.objectId = objectId;
	}

	@Override
	public String toString() {
		return "ObjectTargeter [objectId=" + objectId + "]";
	}
}
