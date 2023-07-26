package resumeBuilder.data;


public class Resume {
	private ContactInfo contactInfo;
	private String objective;
	private String summary;
	private Education[] userEducation;
	private WorkExperience[] workExperience;
	private Skill[] skills;
	private Project[] projects;
	private Certification[] certifications;
	private Award[] awards;
	private Publication[] publications;
	private Reference[] references;
	
	public Resume(ContactInfo contactInfo, String objective, String summary, Education[] userEducation,
			WorkExperience[] workExperience, Skill[] skills, Project[] projects, Certification[] certifications,
			Award[] awards, Publication[] publications, Reference[] references) {
		super();
		this.contactInfo = contactInfo;
		this.objective = objective;
		this.summary = summary;
		this.userEducation = userEducation;
		this.workExperience = workExperience;
		this.skills = skills;
		this.projects = projects;
		this.certifications = certifications;
		this.awards = awards;
		this.publications = publications;
		this.references = references;
	}

	public ContactInfo getContactInfo() {
		return contactInfo;
	}
	
	public String getObjective() {
		return objective;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public Education[] getUserEducation() {
		return userEducation;
	}
	
	public WorkExperience[] getWorkExperience() {
		return workExperience;
	}
	
	public Skill[] getSkills() {
		return skills;
	}
	
	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}
	
	public void setObjective(String objective) {
		this.objective = objective;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public void setUserEducation(Education[] userEducation) {
		this.userEducation = userEducation;
	}
	
	public void setWorkExperience(WorkExperience[] workExperience) {
		this.workExperience = workExperience;
	}
	
	public void setSkills(Skill[] skills) {
		this.skills = skills;
	}

	public Project[] getProjects() {
		return projects;
	}

	public void setProjects(Project[] projects) {
		this.projects = projects;
	}

	public Certification[] getCertifications() {
		return certifications;
	}

	public void setCertifications(Certification[] certifications) {
		this.certifications = certifications;
	}

	public Award[] getAwards() {
		return awards;
	}

	public void setAwards(Award[] awards) {
		this.awards = awards;
	}

	public Publication[] getPublications() {
		return publications;
	}

	public void setPublications(Publication[] publications) {
		this.publications = publications;
	}

	public Reference[] getReferences() {
		return references;
	}

	public void setReferences(Reference[] references) {
		this.references = references;
	}
	
}



