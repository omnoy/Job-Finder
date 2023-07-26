package superapp.data;

import java.util.Objects;

public class SuperAppObjectIdEntity {
	private String superapp;
	private String internalObjectId;
	
	public String getSuperapp() {
		return superapp;
	}

	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
	public String getInteralObjectId() {
		return internalObjectId;
	}
	public void setInternalObjectId(String interalObjectId) {
		this.internalObjectId = interalObjectId;
	}
	
	@Override
	public String toString() {
		return "SuperAppObjectIdEntity [superapp=" + superapp + ", interalObjectId=" + internalObjectId + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(internalObjectId, superapp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuperAppObjectIdEntity other = (SuperAppObjectIdEntity) obj;
		return Objects.equals(internalObjectId, other.internalObjectId) && Objects.equals(superapp, other.superapp);
	}

	
}
