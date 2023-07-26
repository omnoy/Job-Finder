package resumeBuilder.command;

import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import resumeBuilder.data.Award;
import resumeBuilder.data.Certification;
import resumeBuilder.data.ContactInfo;
import resumeBuilder.data.Education;
import resumeBuilder.data.Project;
import resumeBuilder.data.Publication;
import resumeBuilder.data.Reference;
import resumeBuilder.data.Resume;
import resumeBuilder.data.Skill;
import resumeBuilder.data.WorkExperience;
import java.util.Date;


public class ResumeBuildCommand {
	private Resume resume;
	private ChatGPT chat;
	private ObjectMapper jackson;
	private String contextMessage;
	
	public static Resume hardCode() {
	    // Create ContactInfo object
	    ContactInfo contactInfo = new ContactInfo();
	    contactInfo.setFullName("John Doe");
	    contactInfo.setPhoneNumber("123456789");
	    contactInfo.setEmailAddress("johndoe@example.com");
	    contactInfo.setAddressLocation("New York");

	    // Create Education array
	    Education[] userEducation = new Education[2];
	    Education education1 = new Education();
	    education1.setDegree("Bachelor of Science");
	    education1.setInstitution("University of XYZ");
	    education1.setGraduationYear("2015");

	    Education education2 = new Education();
	    education2.setDegree("Master of Science");
	    education2.setInstitution("ABC University");
	    education2.setGraduationYear("2017");

	    userEducation[0] = education1;
	    userEducation[1] = education2;

	    // Create WorkExperience array
	    WorkExperience[] workExperience = new WorkExperience[2];
	    WorkExperience experience1 = new WorkExperience();
	    experience1.setCompanyName("XYZ Corporation");
	    experience1.setRole("Software Engineer");
	    experience1.setStartDate(new Date());
	    experience1.setEndDate(new Date());

	    WorkExperience experience2 = new WorkExperience();
	    experience2.setCompanyName("ABC Inc.");
	    experience2.setRole("Senior Developer");
	    experience2.setStartDate(new Date());

	    workExperience[0] = experience1;
	    workExperience[1] = experience2;

	    // Create Skill array
	    Skill[] skills = new Skill[2];
	    Skill skill1 = new Skill();
	    skill1.setSkillName("Java");
	    skill1.setProficiency("Advanced");

	    Skill skill2 = new Skill();
	    skill2.setSkillName("Spring Framework");
	    skill2.setProficiency("Intermediate");

	    skills[0] = skill1;
	    skills[1] = skill2;

	    // Create Project array
	    Project[] projects = new Project[1];
	    Project project = new Project();
	    project.setName("E-commerce Website");
	    project.setDescription("Developed a fully functional e-commerce website using Java and Spring");
	    project.setUrl("https://example.com/ecommerce");

	    projects[0] = project;

	    // Create Certification array
	    Certification[] certifications = new Certification[1];
	    Certification certification = new Certification();
	    certification.setName("Oracle Certified Java Developer");
	    certification.setOrganization("Oracle");
	    certification.setYear("2016");

	    certifications[0] = certification;

	    // Create Award array
	    Award[] awards = new Award[1];
	    Award award = new Award();
	    award.setName("Best Employee of the Year");
	    award.setIssuer("XYZ Corporation");
	    award.setYear("2019");

	    awards[0] = award;

	    // Create Publication array
	    Publication[] publications = new Publication[1];
	    Publication publication = new Publication();
	    publication.setTitle("Research Paper on Artificial Intelligence");
	    publication.setAuthor("John Doe, Jane Smith");
	    publication.setYear("2022");

	    publications[0] = publication;

	    // Create Reference array
	    Reference[] references = new Reference[1];
	    Reference reference = new Reference();
	    reference.setName("Jane Smith");
	    reference.setDesignation("Senior Manager");
	    reference.setCompany("ABC Inc.");
	    reference.setPhoneNumber("987654321");
	    reference.setEmailAddress("janesmith@example.com");

	    references[0] = reference;
	    
	    return new Resume(contactInfo, null, null, userEducation, workExperience, skills, projects, certifications, awards, publications, references);
	}
	
	
	public ResumeBuildCommand() {
		this.chat = ChatGPT.builder()
				.apiKey("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")
				.build()
				.init();
		this.jackson = new ObjectMapper();
		this.resume = ResumeBuildCommand.hardCode();
		this.contextMessage = "As a professional resume builder, you receive a JSON file containing user data."
				+ " Your task is to generate a nicely formatted HTML resume for the user. The resume should fit"
				+ " within a single page and exclude any null fields. Field names of null fields should not be displayed"
				+ " in the output HTML.\n"
				+ " Please make sure to create an aesthetically pleasing CSS code that complements the HTML resume."
				+ " The CSS should be specifically tailored for a resume, ensuring that the overall design is "
				+ " professional and visually appealing. Consider using appropriate fonts, colors, spacing, and "
				+ " layout techniques to present the resume in an attractive manner.";
	}
	
	public ResumeBuildCommand (ContactInfo contactInfo, String objective, String summary, Education[] userEducation,
			WorkExperience[] workExperience, Skill[] skills, Project[] projects, Certification[] certifications,
			Award[] awards, Publication[] publications, Reference[] references) {
		super();
		this.resume = new Resume(contactInfo, objective, summary, userEducation, workExperience, skills, 
				projects, certifications, awards, publications, references);
		this.chat = ChatGPT.builder()
				.apiKey("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")
				.build()
				.init();
	}
	
	public String execute() {
		// TODO replace the string "Resume JSON" with the actual Resume JSON
		try {
			String resumeJson = this.jackson.writeValueAsString(this.resume);
			Message context = Message.ofSystem(this.contextMessage);
			Message message = Message.of(resumeJson);
			
			ChatCompletion chatCompletion = ChatCompletion.builder()
	                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
	                .messages(Arrays.asList(context, message))
	                .maxTokens(3000)
	                .temperature(0)
	                .build();
	        ChatCompletionResponse response = this.chat.chatCompletion(chatCompletion);
	        Message res = response.getChoices().get(0).getMessage();
	        System.err.println(res.getContent());
	        return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
