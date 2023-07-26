
package superapp.admin_api_tests;

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

/** This test-case contains tests regarding the User Creation feature.
 * 
 * @author Ido Ronen
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ExportAllUsersTests {
    private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private NewUserBoundary newTestAdminUser;
	private UserBoundary testAdminUser;
	private String testAdminUserEmail;
	private String springApplicationName;
	
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
		this.newTestAdminUser = new NewUserBoundary();
		
		newTestAdminUser.setEmail(this.testAdminUserEmail);
		newTestAdminUser.setUsername("Admin Smith");
		newTestAdminUser.setAvatar("A");
		newTestAdminUser.setRole("ADMIN");
		
		//Add admin user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", newTestAdminUser, UserBoundary.class);
		
		//Also get user from database 
		this.testAdminUser = this.restTemplate
				.getForObject(this.baseUrl + "/users/login/{superapp}/{email}", 
						UserBoundary.class, this.springApplicationName, this.testAdminUserEmail);
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
	@DisplayName("Test export all users with user id not found fails")
	public void testExportAllUsersWithUserIdNotFoundFails() {
		// GIVEN the server is up
		// AND the USERS collection does not contain a user with the email "jill@demo.org"
		
		// WHEN I GET /superapp/admin/users?userSuperapp=2023b.ido.ronen&userEmail=jill@demo.org
		
		// THEN the server responds with 403 - FORBIDDEN
		
		assertThatThrownBy(()->
		this.restTemplate.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}", 
				UserBoundary[].class, this.springApplicationName, "jill@demo.org"))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value()); 
		
	}
	
	@Test
	@DisplayName("Test export all users with unauthorized user role fails")
	public void testExportAllUsersWithUnauthorizedUserRoleFails() {
		// GIVEN the server is up
		// AND the USERS collection contains a user containing the following fields:
		//	{
		//		"userId": {
		//			"superapp": "2023b.ido.ronen",
		//			"email": "jill@demo.org"
		//		},
		//		"role":"MINIAPP_USER" //TODO check if we need a test for superapp user too
		//	}
		
		NewUserBoundary testUser = new NewUserBoundary();
		String testUserEmail = "jill@demo.org";
		testUser.setUsername("Jill");
		testUser.setEmail(testUserEmail);
		testUser.setAvatar("J");
		testUser.setRole("MINIAPP_USER");
		this.restTemplate.postForObject(this.baseUrl + "/users", testUser, UserBoundary.class);
		
		// WHEN I GET /superapp/admin/users?userSuperapp=2023b.ido.ronen&userEmail=jill@demo.org
		
		// THEN the server responds with 403 - FORBIDDEN
		
		assertThatThrownBy(()->
		this.restTemplate.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}",
				UserBoundary[].class, this.springApplicationName, testUserEmail))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value()); 
	}
	
	
	
	@Test
	@DisplayName("Test export all users with existing user in the users collection succeeds")
	public void testExportAllUsersWithExistingUserInTheUsersCollectionSucceeds() {
		// GIVEN the server is up
		// AND the USERS collection contains at least one UserEntity (Admin user)
		
		// WHEN  I GET /superapp/admin/users
		
		UserBoundary[] allUsersInDatabase = this.restTemplate
				.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}", UserBoundary[].class,
						this.springApplicationName, this.testAdminUserEmail);
		
		
		// THEN the server responds with 200 - OK
		// AND the USERS collection contains only that user
		
		assertThat(allUsersInDatabase)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testAdminUser);
	}
	
	//TODO pagination tests?
	
	
	
	
}
