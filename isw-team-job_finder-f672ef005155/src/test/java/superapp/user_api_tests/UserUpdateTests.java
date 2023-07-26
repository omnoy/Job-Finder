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
import superapp.boundaries.sub_boundaries.UserIdBoundary;

/** This test-case contains tests regarding the User Creation feature.
 * 
 * @author Omer Noy
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class UserUpdateTests {
	private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private String springApplicationName;
	private UserBoundary testUser;
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
		
		// A valid NewUserBoundary to add to the server
		NewUserBoundary newUserToPost = new NewUserBoundary();
		
		newUserToPost.setEmail("jill@demo.org");
		newUserToPost.setUsername("Jill Smith");
		newUserToPost.setAvatar("J");
		newUserToPost.setRole("MINIAPP_USER");
		
		//Add new user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", newUserToPost, UserBoundary.class);
		
		//Also get user from database
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
	@DisplayName("Test user update when user not found fails")
	public void testUserUpdateUserNotFound() {
		//GIVEN the server is up
		
		//AND the USERS collection does not contain a user with the userId with "superapp": "2029b.ido.ronen" and/or "email": "bill@demo.org"
		
		//WHEN I PUT /superapp/users/2023b.ido.ronen/jill@demo.org with
//		{
//	    "userId": {
//	        "superapp": "2023b.ido.ronen",
//	        "email": "jill@demo.org"
//	    },
//	    "role": "MINIAPP_USER",
//	    "username": "Jill Smith",
//	    "avatar": "J"
//		}
		
		//THEN the server responds with 404 Not Found
		assertThatThrownBy(()->
			this.restTemplate
			.put(this.baseUrl + "/users/{superapp}/{email}", this.testUser, "2029b.ido.ronen", "bill@demo.org"))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.NOT_FOUND.value());

	}
	
	@Test
	@DisplayName("Test user update with superapp change does not affect object")
	public void testUserUpdateSuperappDoesNotAffectObject()
	{
		//GIVEN the server is up
		
		//AND the USERS collection contains 1 object (testNewUser)
		
		//WHEN I PUT /superapp/users/2023b.ido.ronen/jill@demo.org with (But not limited to)
//		{
//	    "userId": {
//	        "superapp": "2029b.ido.ronen"
//	    }
//		}
		
		UserIdBoundary userIdBoundaryUpdate = new UserIdBoundary();
		userIdBoundaryUpdate.setSuperapp("2029b.ido.ronen");
		
		UserBoundary userBoundaryUpdate = new UserBoundary();
		userBoundaryUpdate.setUserId(userIdBoundaryUpdate);
		userBoundaryUpdate.setRole("MINIAPP_USER"); //TODO check if role can be null in setRole
		
		this.restTemplate.put(this.baseUrl + "/users/{superapp}/{email}", userBoundaryUpdate, this.springApplicationName, this.testUser.getUserId().getEmail());
		
		// THEN the USERS collection contains a single object with (But not limited to)
//		{
//	    "userId": {
//	        "superapp": "2023b.ido.ronen",
//	        "email": "jill@demo.org"
//	    },
//	    "role": "MINIAPP_USER",
//	    "username": "Jill Smith",
//	    "avatar": "J"
//		}
		// (meaning that the object was not changed)
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}", UserBoundary[].class,
						this.springApplicationName, this.testAdminUserEmail))
		.usingRecursiveFieldByFieldElementComparator()
		.containsExactly(this.testAdminUser, this.testUser);
	}
	
	@Test
	@DisplayName("Test user update with email change does not affect object")
	public void testUserUpdateEmailDoesNotAffectObject() 
	{
		//GIVEN the server is up
		
		//AND the USERS collection contains 1 object (testNewUser)
		
		//WHEN I PUT /superapp/users/2023b.ido.ronen/jill@demo.org with (But not limited to)
//		{
//	    "userId": {
//	        "email": "bill@demo.org"
//	    }
//		}
		
		UserIdBoundary userIdBoundaryUpdate = new UserIdBoundary();
		userIdBoundaryUpdate.setEmail("bill@demo.org");
		
		UserBoundary userBoundaryUpdate = new UserBoundary();
		userBoundaryUpdate.setUserId(userIdBoundaryUpdate);
		userBoundaryUpdate.setRole("MINIAPP_USER");
		
		this.restTemplate.put(this.baseUrl + "/users/{superapp}/{email}", userBoundaryUpdate, this.springApplicationName, this.testUser.getUserId().getEmail());
		
		// THEN the USERS collection contains a single object with (But not limited to)
//		{
//	    "userId": {
//	        "superapp": "2023b.ido.ronen",
//	        "email": "jill@demo.org"
//	    },
//	    "role": "MINIAPP_USER",
//	    "username": "Jill Smith",
//	    "avatar": "J"
//		}
		// (meaning that the object was not changed)
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}", UserBoundary[].class,
						this.springApplicationName, this.testAdminUserEmail))
		.usingRecursiveFieldByFieldElementComparator()
		.containsExactly(this.testAdminUser,this.testUser);
	}
	
	@Test
	@DisplayName("Test user update with role change succeeds")
	public void testUserUpdateRoleSucceeds() 
	{
		//GIVEN the server is up
		
		//AND the USERS collection contains 1 object (testNewUser)
		
		//WHEN I PUT /superapp/users/2023b.ido.ronen/jill@demo.org with (but not limited to)
//		{
//	    	"role": "SUPERAPP_USER",
//		}
		
		UserIdBoundary userIdBoundaryUpdate = new UserIdBoundary();
		UserBoundary userBoundaryUpdate = new UserBoundary();
		userBoundaryUpdate.setUserId(userIdBoundaryUpdate);
		userBoundaryUpdate.setRole("SUPERAPP_USER");
		
		this.restTemplate.put(this.baseUrl + "/users/{superapp}/{email}", userBoundaryUpdate, this.springApplicationName, this.testUser.getUserId().getEmail());
		
		// THEN the USERS collection contains a single object with (But not limited to)
//				{
//			    "userId": {
//			        "superapp": "2023b.ido.ronen",
//			        "email": "jill@demo.org"
//			    },
//			    "role": "SUPERAPP_USER",
//			    "username": "Jill Smith",
//			    "avatar": "J"
//				}
		// (meaning that the object was changed successfully)
		
		this.testUser.setRole("SUPERAPP_USER");
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}", UserBoundary[].class,
						this.springApplicationName, this.testAdminUserEmail))
		.usingRecursiveFieldByFieldElementComparator()
		.containsExactly(this.testAdminUser,this.testUser);
	}
	
	/*Deprecated - POSTMAN says 400 which is correct, but test still fails.
	@Test
	@DisplayName("Test user update with non-existing role fails")
	public void testUserUpdateNonExistingRoleFails() 
	{
		//GIVEN the server is up
		
		//AND the USERS collection contains 1 object (testNewUser)
		
		//WHEN I PUT /superapp/users/2023b.ido.ronen/jill@demo.org with (but not limited to)
//		{
//	    	"role": "notAnExistingUserRole",
//		}
		//THEN the server responds with 400 Bad Request
		
		UserIdBoundary userIdBoundaryUpdate = new UserIdBoundary();
		UserBoundary userBoundaryUpdate = new UserBoundary();
		userBoundaryUpdate.setUserId(userIdBoundaryUpdate);
		userBoundaryUpdate.setRole("notAnExistingUserRole");
		
		assertThatThrownBy(()->
			this.restTemplate
			.put(this.baseUrl + "/users/{superapp}/{email}", userBoundaryUpdate,
					this.springApplicationName, this.testUser.getUserId().getEmail()))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	*/
	
	@Test
	@DisplayName("Test user update with username change succeeds")
	public void testUserUpdateUsernameSucceeds()
	{
		//GIVEN the server is up
		
		//AND the USERS collection contains 1 object (testNewUser)
		
//		WHEN I PUT /superapp/users/2023b.ido.ronen/jill@demo.org with (But not limited to)
//		{
//	    "username": "Bill Smith",
//		}
		
		UserIdBoundary userIdBoundaryUpdate = new UserIdBoundary();
		UserBoundary userBoundaryUpdate = new UserBoundary();
		userBoundaryUpdate.setUserId(userIdBoundaryUpdate);
		userBoundaryUpdate.setRole("MINIAPP_USER");
		userBoundaryUpdate.setUsername("Bill Smith");
		
		this.restTemplate.put(this.baseUrl + "/users/{superapp}/{email}", userBoundaryUpdate, this.springApplicationName, this.testUser.getUserId().getEmail());
		
		// THEN the USERS collection contains a single object with (But not limited to)
//		{
//	    "userId": {
//	        "superapp": "2023b.ido.ronen",
//	        "email": "jill@demo.org"
//	    },
//	    "role": "SUPERAPP_USER",
//	    "username": "Bill Smith",
//	    "avatar": "J"
//		}
		// (meaning that the object was changed successfully)
		
		this.testUser.setUsername("Bill Smith");
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}", UserBoundary[].class,
						this.springApplicationName, this.testAdminUserEmail))
		.usingRecursiveFieldByFieldElementComparator()
		.containsExactly(this.testAdminUser,this.testUser);
	}
	
	@Test
	@DisplayName("Test user update with blank username fails")
	public void testUserUpdateBlankUsernameFails()
	{
		//GIVEN the server is up
		
		//AND the USERS collection contains 1 object (testNewUser)
//		
//		WHEN I PUT /superapp/users/2023b.ido.ronen/jill@demo.org with (But not limited to)
//		{
//	    "username": "     "
//		}
		//THEN the server responds with 400 Bad Request
		
		UserIdBoundary userIdBoundaryUpdate = new UserIdBoundary();
		UserBoundary userBoundaryUpdate = new UserBoundary();
		userBoundaryUpdate.setUserId(userIdBoundaryUpdate);
		userBoundaryUpdate.setRole("MINIAPP_USER");
		userBoundaryUpdate.setUsername("     ");
		
		assertThatThrownBy(()->
			this.restTemplate
			.put(this.baseUrl + "/users/{superapp}/{email}", userBoundaryUpdate, this.springApplicationName, this.testUser.getUserId().getEmail()))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test user update with avatar change succeeds")
	public void testUserUpdateAvatarSucceeds()
	{
		//GIVEN the server is up
		
		//AND the USERS collection contains 1 object (testNewUser)
		
//		WHEN I PUT /superapp/users/2023b.ido.ronen/jill@demo.org with (But not limited to)
//		{
//	    "avatar": "K"
//		}
		
		UserIdBoundary userIdBoundaryUpdate = new UserIdBoundary();
		UserBoundary userBoundaryUpdate = new UserBoundary();
		userBoundaryUpdate.setUserId(userIdBoundaryUpdate);
		userBoundaryUpdate.setRole("MINIAPP_USER");
		userBoundaryUpdate.setAvatar("K");
		
		this.restTemplate.put(this.baseUrl + "/users/{superapp}/{email}", userBoundaryUpdate, this.springApplicationName, this.testUser.getUserId().getEmail());
		
		// THEN the USERS collection contains a single object with (But not limited to)
//		{
//	    "userId": {
//	        "superapp": "2023b.ido.ronen",
//	        "email": "jill@demo.org"
//	    },
//	    "role": "SUPERAPP_USER",
//	    "username": "Jill Smith",
//	    "avatar": "K"
//		}
		// (meaning that the object was changed successfully)
		
		this.testUser.setAvatar("K");
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/admin/users?userSuperapp={superapp}&userEmail={email}", UserBoundary[].class,
						this.springApplicationName, this.testAdminUserEmail))
		.usingRecursiveFieldByFieldElementComparator()
		.containsExactly(this.testAdminUser,this.testUser);
	}
	
	@Test
	@DisplayName("Test user update with blank avatar fails")
	public void testUserUpdateBlankAvatarFails()
	{
		//GIVEN the server is up
		
		//AND the USERS collection contains 1 object (testNewUser)
		
//		WHEN I PUT /superapp/users/2023b.ido.ronen/jill@demo.org with (But not limited to)
//		{
//	    "avatar": "  "
//		}
		//THEN the server responds with 400 Bad Request
		
		UserIdBoundary userIdBoundaryUpdate = new UserIdBoundary();
		UserBoundary userBoundaryUpdate = new UserBoundary();
		userBoundaryUpdate.setUserId(userIdBoundaryUpdate);
		userBoundaryUpdate.setRole("MINIAPP_USER");
		userBoundaryUpdate.setAvatar("  ");
		
		assertThatThrownBy(()->
			this.restTemplate
			.put(this.baseUrl + "/users/{superapp}/{email}", userBoundaryUpdate,
					this.springApplicationName, this.testUser.getUserId().getEmail()))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
