package resumeBuilder.data;


public class ContactInfo {
	private String fullName;
	private String phoneNumber;
	private String emailAddress;
	private String addressLocation;
	
	public ContactInfo() {};
	
	public String getFullName() {
		return fullName;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public String getAddressLocation() {
		return addressLocation;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public void setAddressLocation(String addressLocation) {
		this.addressLocation = addressLocation;
	}
	
	
	
}
