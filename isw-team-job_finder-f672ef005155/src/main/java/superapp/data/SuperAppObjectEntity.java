package superapp.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "SUPERAPPOBJECTS")
public class SuperAppObjectEntity {
	@Id private SuperAppObjectIdEntity objectId;
	private String type;
	private String alias;
	private boolean active;
	private Date creationTimeStamp;
	
	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2D) private double[] location;
	
	private UserIdEntity createdBy;
	private Map<String,Object> objectDetails;
	
	@DBRef private Set<SuperAppObjectEntity> children;
	@DBRef private Set<SuperAppObjectEntity> parents;
	
	public SuperAppObjectEntity() {
		this.children = new HashSet<>();
		this.parents = new HashSet<>();
		this.location = new double[2];
	}
	
	public SuperAppObjectIdEntity getObjectId() {
		return objectId;
	}
	public void setObjectId(SuperAppObjectIdEntity objectId) {
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
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Date getCreationTimeStamp() {
		return creationTimeStamp;
	}
	public void setCreationTimeStamp(Date creationTimeStamp) {
		this.creationTimeStamp = creationTimeStamp;
	}
	public double[] getLocation() {
		return location;
	}
	public void setLocation(double[] location) {
		this.location = location;
	}
	public UserIdEntity getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(UserIdEntity createdBy) {
		this.createdBy = createdBy;
	}
	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}
	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}
	
	public Set<SuperAppObjectEntity> getChildren() {
		return children;
	}
	public void setChildren(Set<SuperAppObjectEntity> children) {
		this.children = children;
	}
	public Set<SuperAppObjectEntity> getParents() {
		return parents;
	}
	public void setParents(Set<SuperAppObjectEntity> parents) {
		this.parents = parents;
	}
	
	public void addChild(SuperAppObjectEntity child) {
		this.children.add(child);
	}
	public void addParent(SuperAppObjectEntity parent) {
		this.parents.add(parent);
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(objectId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuperAppObjectEntity other = (SuperAppObjectEntity) obj;
		return Objects.equals(objectId, other.objectId);
	}

	@Override
	public String toString() {
		return "SuperAppObjectEntity [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", active="
				+ active + ", creationTimeStamp=" + creationTimeStamp + ", location=" + location + ", createdBy="
				+ createdBy + ", objectDetails=" + objectDetails + ", children=" + children + ", parents=" + parents
				+ "]";
	}

}
