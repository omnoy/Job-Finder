package superapp.user_api_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.NewUserBoundary;
import superapp.boundaries.UserBoundary;

/** This test-case contains tests regarding the User Login feature.
 * 
 * @author Ido Ronen
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class UserLoginTests {
	private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private String springApplicationName;
	private UserBoundary testUser;
	private String testAdminUserEmail;
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@Value("${spring.application.name}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}
	
	@PostConstruct
	public void setup() {
		this.restTemplate = new RestTemplate();
		this.baseUrl = "http://localhost:" + this.port + "/superapp";
	}
	
	
	@BeforeEach
	public void resetState() {
		// Tear down DB:
		tearDown();
		
		// Also, create/reset some basic instances for code-simplicity:
		// Admin User Email for authorization
		this.testAdminUserEmail = "admin@demo.org";
		
		// A valid NewUserBoundary with the ADMIN role in order to perform tests
		NewUserBoundary newTestAdminUser = new NewUserBoundary();
		
		newTestAdminUser.setEmail(this.testAdminUserEmail);
		newTestAdminUser.setUsername("Admin Smith");
		newTestAdminUser.setAvatar("A");
		newTestAdminUser.setRole("ADMIN");
		
		//Add new user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", newTestAdminUser, UserBoundary.class);

		// A valid new user boundary to post to the server
		NewUserBoundary newUserToPost = new NewUserBoundary();
		
		newUserToPost.setEmail("jill@demo.org");
		newUserToPost.setUsername("Jill Smith");
		newUserToPost.setAvatar("J");
		newUserToPost.setRole("MINIAPP_USER");
	
		this.restTemplate.postForObject(this.baseUrl + "/users", newUserToPost, UserBoundary.class);
		
		//Get UserBoundary from the server
		this.testUser = this.restTemplate.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}", UserBoundary[].class,
				this.springApplicationName, this.testAdminUserEmail)[1];
	}
	
	@AfterAll
	public void tearDown() {
		// tearDownAdmin
		NewUserBoundary tearDownAdminUser = new NewUserBoundary();
		
		String tearDownAdminEmail = "tear@down.org";
		
		tearDownAdminUser.setEmail(tearDownAdminEmail);
		tearDownAdminUser.setUsername("Tear Down");
		tearDownAdminUser.setAvatar("TD");
		tearDownAdminUser.setRole("ADMIN");
		
		//Add new user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", tearDownAdminUser, UserBoundary.class);
		
		this.restTemplate.delete(this.baseUrl + "/admin/objects?userSuperapp={superapp}&userEmail={email}",
				this.springApplicationName, tearDownAdminEmail);
		this.restTemplate.delete(this.baseUrl + "/admin/miniapp?userSuperapp={superapp}&userEmail={email}",
				this.springApplicationName, tearDownAdminEmail);
		//delete users last since it contains the admin user
		this.restTemplate.delete(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}",
				this.springApplicationName, tearDownAdminEmail);
	}
	
	
	@Test
	@DisplayName("Test user login succeeds")
	public void testUserLoginSucceeds() {
		// GIVEN the server is up
		// AND the USERS collection already contains at least 1 UserEntity (testUser)

		// WHEN I GET /superapp/users/login/2023b.ido.ronen/jill@demo.org
		
		// THEN I should get the JSON:
//  {
//	    "userId": {
//	        		"superapp": "2023b.ido.ronen",
//	        		"email": "jill@demo.org"
//	    		  },
//	    "role": "MINIAPP_USER",
//	    "username": "Jill Smith",
//	    "avatar": "J"
//	}
	   // AND the server responds with 200 - OK


		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/users/login/{superapp}/{email}", UserBoundary.class,
						this.springApplicationName , this.testUser.getUserId().getEmail())
			.equals(testUser));
	}
	
	@Test
	@DisplayName("Test user login not found fails")
	public void testUserLoginFails() {
		// GIVEN the server is up
		//AND the USERS collection does not contain a user with the userId with "superapp": "2029b.ido.ronen" and/or "email": "bill@demo.org"
			
		// WHEN I GET /superapp/users/login/2023b.ido.ronen/jill@demo.org
		
		// THEN the server responds with 404 - Not Found
		assertThatThrownBy(()->
		this.restTemplate
			.getForObject(this.baseUrl+ "/users/login/{superapp}/{email}", UserBoundary.class,
					"2029b.ido.ronen", "bill@demo.org"))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	
}
