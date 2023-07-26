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
import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.UserBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectCreatorBoundary;

/** This test-case contains tests regarding the User Creation feature.
 * 
 * @author Ido Ronen
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class DeleteAllObjectsTests {
    private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private String springApplicationName;
	private String testAdminUserEmail; 
	
	private UserBoundary testSuperappUser;
	private String testSuperappUserEmail;
	
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
		NewUserBoundary newUserToPost = new NewUserBoundary();
		
		newUserToPost.setEmail(this.testAdminUserEmail);
		newUserToPost.setUsername("Admin Smith");
		newUserToPost.setAvatar("A");
		newUserToPost.setRole("ADMIN");
		
		//Add new user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", newUserToPost, UserBoundary.class);
		
		// A valid NewUserBoundary with the SUPERAPP_USER role in order to export all objects
		// SuperApp User Email for authorization
		this.testSuperappUserEmail = "superapp@demo.org";
		
		// A valid NewUserBoundary with the ADMIN role in order to perform tests
		NewUserBoundary newTestSuperappUser = new NewUserBoundary();
		
		newTestSuperappUser.setEmail(this.testSuperappUserEmail);
		newTestSuperappUser.setUsername("Superapp Smith");
		newTestSuperappUser.setAvatar("S");
		newTestSuperappUser.setRole("SUPERAPP_USER");
		
		//Add new user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", newTestSuperappUser, UserBoundary.class);
		
		//Also get user from database 
		this.testSuperappUser = this.restTemplate
				.getForObject(this.baseUrl + "/users/login/{superapp}/{email}", 
						UserBoundary.class, this.springApplicationName, this.testSuperappUserEmail);
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
	@DisplayName("Test delete all objects with user id not found fails")
	public void testDeleteAllObjectsWithUserIdNotFoundFails() {
		// GIVEN the server is up
		// AND the USERS collection does not contain a user with the email "jill@demo.org"
		
		// WHEN I DELETE /superapp/admin/objects?userSuperapp=2023b.ido.ronen&userEmail=jill@demo.org
		
		// THEN the server responds with 403 - FORBIDDEN
		
		assertThatThrownBy(()->
		this.restTemplate.delete(this.baseUrl + "/admin/objects?userSuperapp={superapp}&userEmail={email}",
				this.springApplicationName, "jill@demo.org"))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value());
		
	}
	
	@Test
	@DisplayName("Test delete all objects with unauthorized user role fails")
	public void testDeleteAllObjectsWithUnauthorizedUserRoleFails() {
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
		testUser.setAvatar("J");
		testUser.setEmail(testUserEmail);
		testUser.setRole("MINIAPP_USER");
		this.restTemplate.postForObject(this.baseUrl + "/users", testUser, UserBoundary.class);
		
		// WHEN I DELETE /superapp/admin/objects?userSuperapp=2023b.ido.ronen&userEmail=jill@demo.org
		
		// THEN the server responds with 403 - FORBIDDEN
		
		assertThatThrownBy(()->
		this.restTemplate.delete(this.baseUrl + "/admin/objects?userSuperapp={superapp}&userEmail={email}",
				this.springApplicationName, testUserEmail))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value());
		
	}
	
	@Test
	@DisplayName("Test delete all objects with empty objects collection succeeds")
	public void testDeleteAllObjectsWithEmptyObjectsCollectionSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection is empty
		
		// WHEN  I DELETE /superapp/admin/objects
		
		this.restTemplate.delete(this.baseUrl + "/admin/objects?userSuperapp={superapp}&userEmail={email}",
				this.springApplicationName, this.testAdminUserEmail);
		
		SuperAppObjectBoundary[] allObjectsInDatabase = this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={superapp}&userEmail={email}", 
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testSuperappUserEmail);
		
		// THEN the server responds with 200 - OK
		// AND the SUPERAPPOBJECTS collection is empty.
		
		assertThat(allObjectsInDatabase)
			.isNotNull()
			.isEmpty();
	}
	
	@Test
	@DisplayName("Test delete all objects with existing objects collection succeeds")
	public void testDeleteAllObjectsWithExistingObjectsInCollectionSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains at least one SuperAppObjectEntity
		
		SuperAppObjectBoundary testObject = new SuperAppObjectBoundary();
		
		testObject.setAlias("Test");
		testObject.setType("testObj");
		
		SuperAppObjectCreatorBoundary creatorBoundary = new SuperAppObjectCreatorBoundary();
		creatorBoundary.setUserId(this.testSuperappUser.getUserId());
		testObject.setCreatedBy(creatorBoundary);
		
		this.restTemplate.postForObject(this.baseUrl + "/objects", testObject, SuperAppObjectBoundary.class);
		
		// WHEN  I DELETE /superapp/admin/objects
		
		this.restTemplate.delete(this.baseUrl + "/admin/objects?userSuperapp={superapp}&userEmail={email}",
				this.springApplicationName, this.testAdminUserEmail);
		
		SuperAppObjectBoundary[] allObjectsInDatabase = this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={superapp}&userEmail={email}", 
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testSuperappUserEmail);
		
		// THEN the server responds with 200 - OK
		// AND the SUPERAPPOBJECTS collection is empty.
		
		assertThat(allObjectsInDatabase)
			.isNotNull()
			.isEmpty();
	}
}
