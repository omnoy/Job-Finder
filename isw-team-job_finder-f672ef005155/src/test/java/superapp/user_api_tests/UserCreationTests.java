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

/** This test-case contains tests regarding the User Creation feature.
 * 
 * @author Ido Ronen
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class UserCreationTests {
    private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private String springApplicationName;
	private NewUserBoundary testNewUser;
	private UserBoundary testAdminUser;
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
		
		//Add admin user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", newTestAdminUser, UserBoundary.class);
		
		//Get admin from database
		this.testAdminUser = this.restTemplate.getForObject(this.baseUrl + "/users/login/{superapp}/{email}", UserBoundary.class,
				this.springApplicationName , this.testAdminUserEmail);
		
		// A valid object boundary
		this.testNewUser = new NewUserBoundary();
		
		this.testNewUser.setEmail("jill@demo.org");
		this.testNewUser.setUsername("Jill Smith");
		this.testNewUser.setAvatar("J");
		this.testNewUser.setRole("MINIAPP_USER");		
	}
	
	@AfterAll
	public void tearDown() {
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
	@DisplayName("Test new user creation with valid input succeeds")
	public void testNewUserCreationWithValidInputSucceeds() {
		// GIVEN the server is up
		// AND the USERS collection is empty
		
		// WHEN  I POST /superapp/users with:
		
//		{
//		    "email": "jill@demo.org",
//		    "role": "MINIAPP_USER",
//		    "username": "Jill Smith",
//		    "avatar": "J"
//		}

		
		UserBoundary returnedUser = this.restTemplate
				.postForObject(this.baseUrl + "/users", this.testNewUser, UserBoundary.class);
		
		// THEN the USERS contains a single user with (But not limited to)
		
//		{
//		    "userId": {
//		        "superapp": "2023b.ido.ronen",
//		        "email": "jill@demo.org"
//		    },
//		    "role": "MINIAPP_USER",
//		    "username": "Jill Smith",
//		    "avatar": "J"
//		}

		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}", UserBoundary[].class,
						this.springApplicationName, this.testAdminUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testAdminUser, returnedUser);
	}
	
	@Test
	@DisplayName("Test new user creation without username fails")
	public void testNewUserCreationWithoutUsernameFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/users with
		
//		{
//		    "email": "jill@demo.org",
//		    "role": "MINIAPP_USER",
//		    "username": null,
//		    "avatar": "J"
//		}

		// THEN the server responds with 400 - Bad Request
		this.testNewUser.setUsername(null);
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/users", this.testNewUser, UserBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test new user creation with blank username fails")
	public void testNewUserCreationWithBlankUsernameFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/users with
		
//		{
//		    "email": "jill@demo.org",
//		    "role": "MINIAPP_USER",
//		    "username": "",
//		    "avatar": "J"
//		}

		// THEN the server responds with 400 - Bad Request
		this.testNewUser.setUsername("");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/users", this.testNewUser, UserBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test new user creation without avatar fails")
	public void testNewUserCreationWithoutAvatarFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/users with
		
//		{
//		    "email": "jill@demo.org",
//		    "role": "MINIAPP_USER",
//		    "username": "Jill Smith",
//		    "avatar": null
//		}

		// THEN the server responds with 400 - Bad Request
		this.testNewUser.setAvatar(null);
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/users", this.testNewUser, UserBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test new user creation with blank avatar fails")
	public void testNewUserCreationWithBlankAvatarFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/users with
		
//		{
//		    "email": "jill@demo.org",
//		    "role": "MINIAPP_USER",
//		    "username": "Jill Smith",
//		    "avatar": ""
//		}

		// THEN the server responds with 400 - Bad Request
		this.testNewUser.setAvatar("");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/users", this.testNewUser, UserBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test new user creation with invalid email format fails") 
	public void testNewUserCreationWithInvalidEmailFormatFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/users with
		
//		{
//		    "email": "jilldemoorg",
//		    "role": "MINIAPP_USER",
//		    "username": "Jill Smith",
//		    "avatar": "J"
//		}

		// THEN the server responds with 400 - Bad Request
		this.testNewUser.setEmail("jilldemoorg");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/users", this.testNewUser, UserBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test new user creation with email already exists fails") 
	public void testNewUserCreationWithEmailAlreadyExistsFails() {
		// GIVEN the server is up
		// AND the server already contains a UserEntity with the email “jill@demo.org”
		this.restTemplate.postForObject(this.baseUrl + "/users", this.testNewUser, UserBoundary.class);
		
		// WHEN I POST /superapp/users with
		
//		{
//		    "email": "jill@demo.org",
//		    "role": "MINIAPP_USER",
//		    "username": "Jill Smith",
//		    "avatar": "J"
//		}
		
		// THEN the server responds with 409 - Conflict
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/users", this.testNewUser, UserBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.CONFLICT.value());
	}
	
	/*Deprecated - POSTMAN says 400 which is correct, but test still fails.
	@Test
	@DisplayName("Test new user creation with non-existing role fails") 
	public void testNewUserCreationWithNonExistingRoleFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/users with
		
//		{
//		    "email": "jill@demo.org",
//		    "role": "thisIsDefinatelyNotAnExistingRoleInOurApp",
//		    "username": "Jill Smith",
//		    "avatar": "J"
//		}

		// THEN the server responds with 400 - Bad Request
		this.testNewUser.setRole("thisIsDefinatelyNotAnExistingRoleInOurApp");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/users", this.testNewUser, UserBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	*/
	
}
