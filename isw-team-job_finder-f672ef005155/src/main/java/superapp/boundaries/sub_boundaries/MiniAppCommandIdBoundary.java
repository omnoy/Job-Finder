package superapp.boundaries.sub_boundaries;

/**
 * The class {@code CommandIdBoundary} is a class which represents a boundary
 * for identifying any {@code MiniAppCommandBoundary} instance. The class follows the
 * Java Beans rules so it could be marshaled and unmarshaled.
 * 
 * <p>
 * The identification of command boundaries in this project has three components:
 * <ul>
 * <li>A string identifier for the project itself, done via {@code superapp}. Any boundaries
 * in the project should have this parameter set to "2023b.ido.ronen".</li>
 * <li>A string identifier for the issuing miniapp, using its name.</li>
 * <li>A integer identifier for a specific command by an internal id, done via {@code internalCommandId}.</li>
 * </ul>
 * </p>
 * 
 * @author 	Rom Gat
 */
public class MiniAppCommandIdBoundary {
	private String superapp;
	private String miniapp;
	private String internalCommandId;
	
	
	public MiniAppCommandIdBoundary() {
	}
	
	public String getSuperapp() {
		return superapp;
	}
	
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
	public String getMiniapp() {
		return miniapp;
	}
	
	public void setMiniapp(String miniapp) {
		this.miniapp = miniapp;
	}
	
	public String getInternalCommandId() {
		return internalCommandId;
	}
	
	public void setInternalCommandId(String internalObjectId) {
		this.internalCommandId = internalObjectId;
	}

	@Override
	public String toString() {
		return "CommandId [superapp=" + superapp + ", miniapp=" + miniapp + ", internalCommandId=" + internalCommandId
				+ "]";
	}
	
	@Override
	public boolean equals(Object o) {
	    if (o == this)
	        return true;
	    if (!(o instanceof MiniAppCommandIdBoundary))
	        return false;
	    
	    MiniAppCommandIdBoundary other = (MiniAppCommandIdBoundary)o;
	    return this.getSuperapp().equals(other.getSuperapp()) && this.getMiniapp().equals(other.getMiniapp())
	    		&& this.getInternalCommandId().equals(other.getInternalCommandId());
	}
	
	@Override
	public int hashCode() {
		int x = getSuperapp().hashCode();
		int y = getMiniapp().hashCode();
		int z = getInternalCommandId().hashCode();
		
		// to hash to naturals into one natural, we'll use Cantor's pairing function with n=3.
		int n2 = ((x + y + 1) * (x + y)) / 2 + y;
		return ((n2 + z + 1) * (n2 + z)) / 2 + z;
	}
}
