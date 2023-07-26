package superapp.boundaries.sub_boundaries;

/**
 * The class {@code LocationBoundary} is a class which represents a boundary
 * for a location in the world. The class follows the Java Beans rules
 * so it could be marshaled and unmarshaled.
 * 
 * <p>
 * Locations in our project are represented via <b>latitude</b> and <b>longitude</b>.
 * </p>
 * 
 * @author 	Omer Noy
 * 			Rom Gat
 */
public class LocationBoundary {
	private Double lat;
	private Double lng;
	
	
	public LocationBoundary() {
		super();
	}

	public Double getLat() {
		return lat;
	}
	
	public void setLat(Double lat) {
		this.lat = lat;
	}
	
	public Double getLng() {
		return lng;
	}
	
	public void setLng(Double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "LocationBoundary [lat=" + lat + ", lng=" + lng + "]";
	}
}
