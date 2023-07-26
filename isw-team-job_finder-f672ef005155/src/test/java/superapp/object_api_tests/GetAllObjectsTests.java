package superapp.object_api_tests;

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
import superapp.boundaries.sub_boundaries.UserIdBoundary;

/** This test-case contains tests regarding the Get All Objects feature.
 * 
 * @author Rom Gat
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class GetAllObjectsTests {
	private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private String springApplicationName;
	private SuperAppObjectBoundary activeTestObject;
	private SuperAppObjectBoundary inactiveTestObject;
	
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
	public void resetState() throws InterruptedException {
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
		objectToPost.setActive(false);
		
		this.inactiveTestObject = this.restTemplate
				.postForObject(this.baseUrl + "/objects", objectToPost, SuperAppObjectBoundary.class);
		
		objectToPost.setActive(true);
		
		this.activeTestObject = this.restTemplate
				.postForObject(this.baseUrl + "/objects", objectToPost, SuperAppObjectBoundary.class);
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
	@DisplayName("Test get all objects with superapp user id and first large page returns all objects")
	public void testSpecificInactiveObjectGetWithSuperappUserIdAndFirstLargePageReturnsAllObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 2 object (activeTestObject and inactiveTestObject)
		// AND the first object posted has active=false
		// AND the second object posted has active=true
		
		// WHEN I GET /superapp/objects?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com&size=3&page=0
		SuperAppObjectBoundary[] retreivedObjectBoundaries = this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}&size={size}&page={page}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.superappUserEmail, 3, 0);
		
		// THEN the server returns a SuperAppObjectBoundary array with (But not limited to internally)
		/*	[
		 * 		{
		 * 			"alias": "I am testing!",
		 * 			"type": "TestObject",
		 * 			"active": true,
		 *	 		"createdBy": {
		 * 				"userId": {
		 *					"superapp": "2023b.ido.ronen",
		 *					"email": "superappUser@gmail.com"
		 *				}
		 * 			}
		 * 		},
		 * 		{
		 * 			"alias": "I am testing!",
		 * 			"type": "TestObject",
		 * 			"active": false,
		 *	 		"createdBy": {
		 * 				"userId": {
		 *					"superapp": "2023b.ido.ronen",
		 *					"email": "superappUser@gmail.com"
		 *				}
		 * 			}
		 * 		}
		 * 	]*/
		// (should include both object in reversed posting order)
		assertThat(retreivedObjectBoundaries)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.activeTestObject, this.inactiveTestObject);
	}
	
	@Test
	@DisplayName("Test get all objects with superapp user id and first small page returns only some objects")
	public void testSpecificInactiveObjectGetWithSuperappUserIdAndFirstSmallPageReturnsOnlySomeObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 2 object (activeTestObject and inactiveTestObject)
		// AND the first object posted has active=false
		// AND the second object posted has active=true
		
		// WHEN I GET /superapp/objects?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com&size=1&page=0
		SuperAppObjectBoundary[] retreivedObjectBoundaries = this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}&size={size}&page={page}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.superappUserEmail, 1, 0);
		
		// THEN the server returns a SuperAppObjectBoundary array with (But not limited to internally)
		/*	[
		 * 		{
		 * 			"alias": "I am testing!",
		 * 			"type": "TestObject",
		 * 			"active": true,
		 *	 		"createdBy": {
		 * 				"userId": {
		 *					"superapp": "2023b.ido.ronen",
		 *					"email": "superappUser@gmail.com"
		 *				}
		 * 			}
		 * 		}
		 * 	]*/
		// (should include both object in reversed posting order)
		assertThat(retreivedObjectBoundaries)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.activeTestObject);
	}
	
	@Test
	@DisplayName("Test get all objects with superapp user id and second small page returns only some objects")
	public void testSpecificInactiveObjectGetWithSuperappUserIdAndSecondSmallPageReturnsOnlySomeObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 2 object (activeTestObject and inactiveTestObject)
		// AND the first object posted has active=false
		// AND the second object posted has active=true
		
		// WHEN I GET /superapp/objects?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com&size=1&page=1
		SuperAppObjectBoundary[] retreivedObjectBoundaries = this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}&size={size}&page={page}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.superappUserEmail, 1, 1);
		
		// THEN the server returns a SuperAppObjectBoundary array with (But not limited to internally)
		/*	[
		 * 		{
		 * 			"alias": "I am testing!",
		 * 			"type": "TestObject",
		 * 			"active": false,
		 *	 		"createdBy": {
		 * 				"userId": {
		 *					"superapp": "2023b.ido.ronen",
		 *					"email": "superappUser@gmail.com"
		 *				}
		 * 			}
		 * 		}
		 * 	]*/
		// (should include both object in reversed posting order)
		assertThat(retreivedObjectBoundaries)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.inactiveTestObject);
	}
	
	@Test
	@DisplayName("Test get all objects with miniapp user id returns only active objects")
	public void testSpecificInactiveObjectGetWithMiniappUserIdReturnsOnlyActiveObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 2 object (activeTestObject and inactiveTestObject)
		// AND the first object posted has active=false
		// AND the second object posted has active=true
		
		// WHEN I GET /superapp/objects?userSuperapp=2023b.ido.ronen&userEmail= miniappUser@gmail.com
		SuperAppObjectBoundary[] retreivedObjectBoundaries = this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.superappUserEmail);
		
		// THEN the server returns a SuperAppObjectBoundary array with (But not limited to internally)
		/*	[
		 * 		{
		 * 			"alias": "I am testing!",
		 * 			"type": "TestObject",
		 * 			"active": true,
		 *	 		"createdBy": {
		 * 				"userId": {
		 *					"superapp": "2023b.ido.ronen",
		 *					"email": "superappUser@gmail.com"
		 *				}
		 * 			}
		 * 		}
		 * 	]*/
		// (should include both object in reversed posting order)
		assertThat(retreivedObjectBoundaries)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.activeTestObject, this.inactiveTestObject);
	}
	
	@Test
	@DisplayName("Test get all objects with non-existing user id fails")
	public void testSpecificInactiveObjectGetWithNonExistingUserIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 2 object (activeTestObject and inactiveTestObject)
		// AND the first object posted has active=false
		// AND the second object posted has active=true
		
		// WHEN I GET /superapp/objects?userSuperapp=2023b.ido.ronen&userEmail=nonExistingUserEmail@gmail.com
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(()->
		this.restTemplate
			.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}",
				SuperAppObjectBoundary[].class, this.springApplicationName, "nonExistingUserEmail@gmail.com"))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test get all objects with admin id fails")
	public void testSpecificInactiveObjectGetWithAdminIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 2 object (activeTestObject and inactiveTestObject)
		// AND the first object posted has active=false
		// AND the second object posted has active=true
		
		// WHEN I GET /superapp/objects?userSuperapp=2023b.ido.ronen&userEmail=admin@gmail.com
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(()->
		this.restTemplate
			.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}",
				SuperAppObjectBoundary[].class, this.springApplicationName, this.adminEmail))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
}
