package superapp.boundaries;

import java.util.Date;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import superapp.boundaries.sub_boundaries.*;

/**
 * The class {@code ObjectBoundary} is a class which represents a boundary for
 * any object in the project. The class follows the Java Beans rules so it could
 * be marshaled and unmarshaled.
 * 
 * <p>
 * This boundary is composed of:
 * <ul>
 * 	<li>An ObjectIdBoundary representing the object's identification</li>
 * 	<li>A String representing the object's type</li>
 * 	<li>A String representing the object's alias (nickname)</li>
 * 	<li>A Boolean representing the object's activity status (true = active, false = not active)</li>
 * 	<li>A Date representing the object's creation time</li>
 * 	<li>A LocationBoundary representing the object's location in the world</li>
 * 	<li>An ObjectCreatorBoundary representing the creating user</li>
 * 	<li>A Map of (String, Object) pairs representing the object's contents</li>
 * </ul>
 * </p>
 * 
 * @author 	Rom Gat
 * 			Omer Noy
 */
public class SuperAppObjectBoundary {
	private SuperAppObjectIdBoundary objectId;
	
	@NotBlank(message = "Type cannot be blank / null")
	private String type;
	
	@NotBlank(message = "Alias cannot be blank / null")
	private String alias;
	private Boolean active;
	private Date creationTimestamp;
	private LocationBoundary location;
	
	@Valid
	@NotNull(message = "CreatedBy cannot be null")
	private SuperAppObjectCreatorBoundary createdBy;
	private Map<String, Object> objectDetails;
	
	
	public SuperAppObjectBoundary() {
		//do nothing
	}
	
	public SuperAppObjectIdBoundary getObjectId() {
		return objectId;
	}
	
	public void setObjectId(SuperAppObjectIdBoundary objectId) {
		this.objectId = objectId;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public Date getCreationTimestamp() {
		return creationTimestamp;
	}
	
	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	
	public LocationBoundary getLocation() {
		return location;
	}
	
	public void setLocation(LocationBoundary location) {
		this.location = location;
	}
	
	public SuperAppObjectCreatorBoundary getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(SuperAppObjectCreatorBoundary createdBy) {
		this.createdBy = createdBy;
	}
	
	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}
	
	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

	@Override
	public String toString() {
		return "SuperAppObjectBoundary [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active=" + active
				+ ", creationTimestamp=" + creationTimestamp + ", location=" + location + ", createdBy=" + createdBy
				+ ", objectDetails=" + objectDetails + "]";
	}
}
