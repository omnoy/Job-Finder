package superapp.object_api_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import superapp.boundaries.sub_boundaries.LocationBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectCreatorBoundary;
import superapp.boundaries.sub_boundaries.UserIdBoundary;

/** This test-case contains tests regarding the Object Creation feature.
 * 
 * @author Rom Gat
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ObjectCreationTests {
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
		
		// Create a valid object boundary
		this.testObject = new SuperAppObjectBoundary();
		
		this.testObject.setAlias("I am testing!");
		this.testObject.setType("TestObject");
		
		UserIdBoundary createdBy = new UserIdBoundary();
		createdBy.setSuperapp(this.springApplicationName);
		createdBy.setEmail(this.superappUserEmail);
		
		SuperAppObjectCreatorBoundary objectCreator = new SuperAppObjectCreatorBoundary();
		objectCreator.setUserId(createdBy);
		
		this.testObject.setCreatedBy(objectCreator);
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
	@DisplayName("Test object creation by superapp user with valid input succeeds")
	public void testObjectCreationBySuperappUserWithValidInputSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection is empty
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		SuperAppObjectBoundary returnedObject = this.restTemplate
				.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class);
		
		// THEN the SUPERAPPOBJECTS contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		returnedObject.setAlias(this.testObject.getAlias());
		returnedObject.setType(this.testObject.getType());
		returnedObject.setCreatedBy(this.testObject.getCreatedBy());
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(returnedObject);
	}
	
	@Test
	@DisplayName("Test object creation by superapp user with valid input matches returned object")
	public void testObjectCreationBySuperappUserWithValidInputMatchesReturnedObject() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection is empty
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		SuperAppObjectBoundary returnedObject = this.restTemplate
				.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class);
		
		// THEN the SUPERAPPOBJECTS contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// (this time, we will make sure that returned object from POST is identical to gotten object)
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(returnedObject);
	}
	
	@Test
	@DisplayName("Test object creation without alias fails")
	public void testObjectCreationWithoutAliasFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": null,
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.setAlias(null);
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with blank alias fails")
	public void testObjectCreationWithBlankAliasFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.setAlias("");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation without type fails")
	public void testObjectCreationWithoutTypeFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": null,
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.setType(null);
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with blank type fails")
	public void testObjectCreationWithBlankTypeFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.setType("");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation without createdBy fails")
	public void testObjectCreationWithoutCreatedByFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": null
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.setCreatedBy(null);
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation without createdBy.userId fails")
	public void testObjectCreationWithoutCreatedByUserIdFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": null
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getCreatedBy().setUserId(null);
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation without createdBy.userId.superapp fails")
	public void testObjectCreationWithoutCreatedByUserIdSuperappFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": null,
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getCreatedBy().getUserId().setSuperapp(null);
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with blank createdBy.userId.superapp fails")
	public void testObjectCreationWithBlankCreatedByUserIdSuperappFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getCreatedBy().getUserId().setSuperapp("");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation by user from different superapp fails")
	public void testObjectCreationByUserFromDifferentSuperappFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.notido.notronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 403 - Forbidden
		this.testObject.getCreatedBy().getUserId().setSuperapp("2023b.notido.notronen");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object creation without createdBy.userId.email fails")
	public void testObjectCreationWithoutCreatedByUserIdEmailFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": null
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getCreatedBy().getUserId().setEmail(null);
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with blank createdBy.userId.email fails")
	public void testObjectCreationWithBlankCreatedByUserIdEmailFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": ""
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getCreatedBy().getUserId().setEmail("");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with createdBy.userId.email not in email format fails")
	public void testObjectCreationWithCreatedByUserIdEmailNotInEmailFormatFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "thisIsNotInEmailFormat"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getCreatedBy().getUserId().setEmail("thisIsNotInEmailFormat");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation by non-existing user fails")
	public void testObjectCreationByNonExistingUserFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "someNonExistingUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 403 - Forbidden
		this.testObject.getCreatedBy().getUserId().setEmail("someNonExistingUser@gmail.com");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object creation by miniapp user fails")
	public void testObjectCreationByMiniAppUserFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "miniAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 403 - Forbidden
		this.testObject.getCreatedBy().getUserId().setEmail("miniAppUser@gmail.com");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object creation by admin fails")
	public void testObjectCreationByAdminFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "admin@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// THEN the server responds with 403 - Forbidden
		this.testObject.getCreatedBy().getUserId().setEmail("admin@gmail.com");
		
		assertThatThrownBy(()->
				this.restTemplate
					.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object creation with valid input and active contains the input active")
	public void testObjectCreationWithValidInputAndActiveContainsTheInputActive() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection is empty
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"active": true,
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		this.testObject.setActive(true);
		
		SuperAppObjectBoundary returnedObject = this.restTemplate
				.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class);
		
		// THEN the SUPERAPPOBJECTS contains a single object with (But not limited to)
		/*	{
		 * 		"active": true
		 * 	}*/
		returnedObject.setActive(this.testObject.getActive());
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(returnedObject);
	}
	
	@Test
	@DisplayName("Test object creation with valid input and location contains the input location")
	public void testObjectCreationWithValidInputAndLocationContainsTheInputLocation() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection is empty
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"location": {
		 * 			"lat": 30.0,
		 * 			"lng": 20.0
		 * 		},
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		LocationBoundary loc = new LocationBoundary();
		loc.setLat(30.0);
		loc.setLng(20.0);
		
		this.testObject.setLocation(loc);
		
		SuperAppObjectBoundary returnedObject = this.restTemplate
				.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class);
		
		// THEN the SUPERAPPOBJECTS contains a single object with (But not limited to)
		/*	{
		 * 		"location": {
		 * 			"lat": 30.0,
		 * 			"lng": 20.0
		 * 		}
		 * 	}*/
		returnedObject.setLocation(this.testObject.getLocation());
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(returnedObject);
	}
	
	@Test
	@DisplayName("Test object creation with valid input and objectDetails contains the input details")
	public void testObjectCreationWithValidInputAndObjectDetailsContainsTheInputDetails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection is empty
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		},
		 * 		"objectDetails": {
		 * 			"foo": true,
		 * 			"goo": 30.2
		 * 		}
		 * 	}*/
		Map<String, Object> details = new HashMap<>();
		details.put("foo", true);
		details.put("goo", 30.2);
		
		this.testObject.setObjectDetails(details);;
		
		SuperAppObjectBoundary returnedObject = this.restTemplate
				.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class);
		
		// THEN the SUPERAPPOBJECTS contains a single object with (But not limited to)
		/*	{
		 * 		"objectDetails": {
		 * 			"foo": true,
		 * 			"goo": 30.2
		 * 		}
		 * 	}*/
		returnedObject.setObjectDetails(this.testObject.getObjectDetails());
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(returnedObject);
	}
	
	@Test
	@DisplayName("Test object creation with valid input contains creationTimestamp equal or greater than before creation time")
	public void testObjectCreationWithValidInputContainsCreationTimestampEqualOrGreaterThanBeforeCreationTime() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection is empty
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// after the Date preCreationDate
		Date preCreationDate = new Date();
		this.restTemplate
				.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class);
		
		// THEN the SUPERAPPOBJECTS contains a single object with (But not limited to)
		/*	{
		 * 		"creationTimestamp": someDate,
		 * 	}*/
		// AND someDate >= preCreationDate
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail)[0]
				.getCreationTimestamp().compareTo(preCreationDate))
			.isGreaterThanOrEqualTo(0);
	}
	
	@Test
	@DisplayName("Test object creation with valid input contains creationTimestamp equal or less than after creation time")
	public void testObjectCreationWithValidInputContainsCreationTimestampEqualOrGreaterThanAfterCreationTime() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection is empty
		
		// WHEN I POST /superapp/objects with
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "superAppUser@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// before the Date postCreationDate
		this.restTemplate
				.postForObject(this.baseUrl + "/objects", this.testObject, SuperAppObjectBoundary.class);
		Date postCreationDate = new Date();
		
		// THEN the SUPERAPPOBJECTS contains a single object with (But not limited to)
		/*	{
		 * 		"creationTimestamp": someDate,
		 * 	}*/
		// AND someDate <= postCreationDate
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail)[0]
				.getCreationTimestamp().compareTo(postCreationDate))
			.isLessThanOrEqualTo(0);
	}
	
}
