package superapp.object_api_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

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
import superapp.boundaries.sub_boundaries.UserIdBoundary;

/** This test-case contains tests regarding the Specific Object Get feature.
 * 
 * @author Rom Gat
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class SpecificObjectGetTests {
	private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private String springApplicationName;
	private SuperAppObjectBoundary testObject;
	
	// For user authentication:
	private String superappUserEmail;
	private String miniappUserEmail;
	private String adminEmail;
	
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
		
		this.superappUserEmail = "superAppUser@gmail.com";
		this.miniappUserEmail = "miniAppUser@gmail.com";
		this.adminEmail = "admin@gmail.com";
	}
	
	@BeforeEach
	public void resetState() {
		// Tear down DB:
		tearDown();
		
		// Create some users in the database to perform actions:
		NewUserBoundary user = new NewUserBoundary();
		
		user.setEmail(this.superappUserEmail);
		user.setUsername("superAppUser");
		user.setAvatar("S");
		user.setRole("SUPERAPP_USER");
		
		this.restTemplate
			.postForObject(baseUrl + "/users", user, UserBoundary.class);
		
		user.setEmail(this.miniappUserEmail);
		user.setUsername("miniAppUser");
		user.setAvatar("M");
		user.setRole("MINIAPP_USER");
		
		this.restTemplate
			.postForObject(baseUrl + "/users", user, UserBoundary.class);
		
		user.setEmail(this.adminEmail);
		user.setUsername("admin");
		user.setAvatar("A");
		user.setRole("ADMIN");
		
		this.restTemplate
			.postForObject(baseUrl + "/users", user, UserBoundary.class);
		
		// Create a valid object boundary and POST it
		SuperAppObjectBoundary objectToPost = new SuperAppObjectBoundary();
		
		objectToPost.setAlias("I am testing!");
		objectToPost.setType("TestObject");
		
		UserIdBoundary createdBy = new UserIdBoundary();
		createdBy.setSuperapp(this.springApplicationName);
		createdBy.setEmail(this.superappUserEmail);
		
		SuperAppObjectCreatorBoundary objectCreator = new SuperAppObjectCreatorBoundary();
		objectCreator.setUserId(createdBy);
		
		objectToPost.setCreatedBy(objectCreator);
		
		this.restTemplate
				.postForObject(this.baseUrl + "/objects", objectToPost, SuperAppObjectBoundary.class);
		
		// Also GET that object from 
		this.testObject = this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail)[0];
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
	@DisplayName("Test specific incactive object get with superapp user id succeeds")
	public void testSpecificInactiveObjectGetWithExistingObjectIdSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND that object has active=false
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com
		// (<IOD> is the internal object id of testObject)
		SuperAppObjectBoundary retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						SuperAppObjectBoundary.class, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the server returns a SuperAppObjectBoundary with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superappUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// (should be identical to the existing testObject in the collection)
		assertThat(retreivedObjectBoundary)
			.usingRecursiveComparison()
			.isEqualTo(this.testObject);
	}
	
	@Test
	@DisplayName("Test specific active object get with superapp user id succeeds")
	public void testSpecificActiveObjectGetWithExistingObjectIdSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND that object has active=true
		this.testObject.setActive(true);
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						this.testObject, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		this.testObject = this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail)[0];
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com
		// (<IOD> is the internal object id of testObject)
		SuperAppObjectBoundary retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						SuperAppObjectBoundary.class, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the server returns a SuperAppObjectBoundary with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superappUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// (should be identical to the existing testObject in the collection)
		assertThat(retreivedObjectBoundary)
			.usingRecursiveComparison()
			.isEqualTo(this.testObject);
	}
	
	@Test
	@DisplayName("Test specific incactive object get with miniapp user id fails")
	public void testSpecificInactiveObjectGetWithMiniappUserIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND that object has active=false
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=miniappUser@gmail.com
		// (<IOD> is the internal object id of testObject)
		// THEN the server responds with 404 - Not Found
		assertThatThrownBy(() ->
			this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						SuperAppObjectBoundary.class, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.miniappUserEmail))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	@DisplayName("Test specific active object get with miniapp user id succeeds")
	public void testSpecificActiveObjectGetWithMiniappUserIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND that object has active=true
		this.testObject.setActive(true);
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						this.testObject, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		this.testObject = this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail)[0];
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=miniappUser@gmail.com
		// (<IOD> is the internal object id of testObject)
		SuperAppObjectBoundary retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						SuperAppObjectBoundary.class, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.miniappUserEmail);
		
		// THEN the server returns a SuperAppObjectBoundary with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superappUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// (should be identical to the existing testObject in the collection)
		assertThat(retreivedObjectBoundary)
			.usingRecursiveComparison()
			.isEqualTo(this.testObject);
	}
	
	@Test
	@DisplayName("Test specific object get with non-existing user id fails")
	public void testSpecificObjectGetWithNonExistingUserIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=someNonExistingUser@gmail.com
		// (<IOD> is the internal object id of testObject)
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(() ->
			this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						SuperAppObjectBoundary.class, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, "someNonExistingUser@gmail.com"))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test specific object get with admin id fails")
	public void testSpecificObjectGetWithAdminIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=someNonExistingUser@gmail.com
		// (<IOD> is the internal object id of testObject)
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(() ->
			this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						SuperAppObjectBoundary.class, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.adminEmail))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test specific object get with non-existing object id fails")
	public void testSpecificObjectGetWithNonExistingObjectIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<NEIOD>?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com
		// (<NEIOD> is the internal object id of a non-existing id)
		// THEN the server responds with 404 - Not Found
		assertThatThrownBy(() ->
			this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						SuperAppObjectBoundary.class, this.springApplicationName, UUID.randomUUID().toString(),
						this.springApplicationName, this.superappUserEmail))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
}