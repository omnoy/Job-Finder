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
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;
import superapp.boundaries.sub_boundaries.UserIdBoundary;

/** This test-case contains tests regarding the Object Update feature.
 * 
 * @author Rom Gat
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ObjectUpdateTests {
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
	@DisplayName("Test object update with empty input does not affect object")
	public void testObjectUpdateWithEmptyInputDoesNotAffectObject() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with {}
		// (<IOD> is the internal object id of testObject)
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the SUPERAPPOBJECTS collection contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "helloWorld@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// (meaning that the object was not changed)
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);
	}
	
	@Test
	@DisplayName("Test object update with new objectId does not affect object")
	public void testObjectUpdateWithNewObjectIdDoesNotAffectObject() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"objectId": {
		 * 			"superapp": "foo",
		 * 			"internalObjectId": "1"
		 * 		}
		 * 	}*/
		// (since database is empty, "1" is not an existing id)
		SuperAppObjectIdBoundary newObjectId = new SuperAppObjectIdBoundary();
		newObjectId.setSuperapp("foo");
		newObjectId.setInternalObjectId("1");
		
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setObjectId(newObjectId);
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the SUPERAPPOBJECTS collection contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "helloWorld@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// (meaning that the object was not changed)
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);
	}
	
	@Test
	@DisplayName("Test object update with new creationTimestamp does not affect object")
	public void testObjectUpdateWithNewCreationTimestampDoesNotAffectObject() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"creationTimestamp": someDate
		 * 	}*/
		// where date is some date in the distant past.
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setCreationTimestamp(new Date(0));
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the SUPERAPPOBJECTS collection contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "helloWorld@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// (meaning that the object was not changed)
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);
	}
	
	@Test
	@DisplayName("Test object update with new createdBy does not affect object")
	public void testObjectUpdateWithNewCreatedByDoesNotAffectObject() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"createdBy": {
		 * 			"userId": {
		 * 				"superapp": "foo",
		 * 				"email": "goodbyeMoon@gmail.com"
		 * 			}
		 * 		}
		 * 	}*/
		UserIdBoundary newUserId = new UserIdBoundary();
		newUserId.setSuperapp("foo");
		newUserId.setEmail("goodbyeMoon@gmail.com");
		
		SuperAppObjectCreatorBoundary newCreatedBy = new SuperAppObjectCreatorBoundary();
		newCreatedBy.setUserId(newUserId);
		
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setCreatedBy(newCreatedBy);
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the SUPERAPPOBJECTS collection contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "helloWorld@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		// (meaning that the object was not changed)
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);
	}
	
	@Test
	@DisplayName("Test object update with blank alias fails")
	public void testObjectUpdateWithBlankAliasFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"alias": ""
		 * 	}*/
		// THEN the server responds with 400 - BadRequest
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setAlias("");
		
		assertThatThrownBy(()->
			this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail))
				.isInstanceOf(HttpClientErrorException.class)
				.extracting("statusCode.value")
				.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object update with blank type fails")
	public void testObjectUpdateWithBlankTypeFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"type": ""
		 * 	}*/
		// THEN the server responds with 400 - BadRequest
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setType("");
		
		assertThatThrownBy(()->
			this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object update with valid alias succeeds")
	public void testObjectUpdateWithValidAliasSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"alias": "This is updated!"
		 * 	}*/
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setAlias("This is updated!");
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the SUPERAPPOBJECTS collection contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "This is updated!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "helloWorld@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		this.testObject.setAlias("This is updated!");
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);
	}
	
	@Test
	@DisplayName("Test object update with valid type succeeds")
	public void testObjectUpdateWithValidTypeSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"type": "UpdatedObject"
		 * 	}*/
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setType("UpdatedObject");
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the SUPERAPPOBJECTS collection contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "UpdatedObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "helloWorld@gmail.com"
		 *			}
		 * 		}
		 * 	}*/
		this.testObject.setType("UpdatedObject");
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);
	}
	
	@Test
	@DisplayName("Test object update with valid active succeeds")
	public void testObjectUpdateWithValidActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"active": !testObject.getActive()
		 * 	}*/
		// (meaning that active is necessarily different)
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setActive(!this.testObject.getActive());
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the SUPERAPPOBJECTS collection contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "helloWorld@gmail.com"
		 *			}
		 * 		},
		 * 		"active": !testObject.getActive()
		 * 	}*/
		this.testObject.setActive(!this.testObject.getActive());
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);
	}
	
	@Test
	@DisplayName("Test object update with valid location succeeds")
	public void testObjectUpdateWithValidLocationSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"location": {
		 * 			"lat": testObject.getLocation().getLat() + 5.0,
		 * 			"lng": testObject.getLocation().getLng() - 5.0
		 * 		}
		 * 	}*/
		// (meaning that location is necessarily different)
		LocationBoundary newLocation = new LocationBoundary();
		newLocation.setLat(this.testObject.getLocation().getLat() + 5.0);
		newLocation.setLng(this.testObject.getLocation().getLng() - 5.0);
		
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setLocation(newLocation);
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the SUPERAPPOBJECTS collection contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "helloWorld@gmail.com"
		 *			}
		 *		},
		 *		"location": {
		 * 			"lat": testObject.getLocation().getLat() + 5.0,
		 * 			"lng": testObject.getLocation().getLng() - 5.0
		 * 		}
		 * 	}*/
		this.testObject.setLocation(newLocation);
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);
	}
	
	@Test
	@DisplayName("Test object update with valid objectDetails succeeds")
	public void testObjectUpdateWithValidObjectDetailsSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=superAppUser@gmail.com with
		// (<IOD> is the internal object id of testObject)
		/* 	{
		 * 		"objectDetails": {
		 * 			"definatelyANewKey": new Object()
		 * 		}
		 * 	}*/
		// (meaning that location is necessarily different)
		Map<String, Object> newObjectDetails = new HashMap<>();
		newObjectDetails.put("definatelyANewKey", "ObjectDetail");
		
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		newObjectBoundary.setObjectDetails(newObjectDetails);
		
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the SUPERAPPOBJECTS collection contains a single object with (But not limited to)
		/*	{
		 * 		"alias": "I am testing!",
		 * 		"type": "TestObject",
		 * 		"createdBy": {
		 * 			"userId": {
		 *				"superapp": "2023b.ido.ronen",
		 *				"email": "helloWorld@gmail.com"
		 *			}
		 *		},
		 *		"objectDetails": {
		 * 			"definatelyANewKey": new Object()
		 * 		}
		 * 	}*/
		this.testObject.setObjectDetails(newObjectDetails);
		
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);
	}
	
	@Test
	@DisplayName("Test object update with non existing id fails")
	public void testObjectUpdateWithNonExistingIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection is empty
		this.restTemplate.delete(this.baseUrl + "/admin/objects?userSuperapp={userSuperapp}&userEmail={userEmail}",
				this.springApplicationName, this.adminEmail);
		
		// WHEN I PUT /superapp/objects/notOurAppName/<NEIOD>/ with {}
		// (<NEIOD> is a non-existing internal object id)
		// THEN the server responds with 404 - NotFound
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		
		assertThatThrownBy(()->
				this.restTemplate
					.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
							newObjectBoundary, "notOurAppName", this.testObject.getObjectId().getInternalObjectId() + "a",
							this.springApplicationName, this.superappUserEmail))
				.isInstanceOf(HttpClientErrorException.class)
				.extracting("statusCode.value")
				.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	@DisplayName("Test object update by non-existing user fails")
	public void testObjectUpdateByNonExistingUserFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=someNonExistingUser@gmail.com with {}
		// (<IOD> is the internal object id of testObject)
		// THEN the server responds with 403 - Forbidden
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		
		assertThatThrownBy(()->
			this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, "someNonExistingUser@gmail.com"))
				.isInstanceOf(HttpClientErrorException.class)
				.extracting("statusCode.value")
				.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object update by miniapp user fails")
	public void testObjectUpdateByMiniappUserFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=miniAppUser@gmail.com with {}
		// (<IOD> is the internal object id of testObject)
		// THEN the server responds with 403 - Forbidden
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		
		assertThatThrownBy(()->
			this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.miniappUserEmail))
				.isInstanceOf(HttpClientErrorException.class)
				.extracting("statusCode.value")
				.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object update by admin fails")
	public void testObjectUpdateByAdminFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD>?userSuperapp=2023b.ido.ronen&userEmail=admin@gmail.com with {}
		// (<IOD> is the internal object id of testObject)
		// THEN the server responds with 403 - Forbidden
		SuperAppObjectBoundary newObjectBoundary = new SuperAppObjectBoundary();
		
		assertThatThrownBy(()->
			this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
						newObjectBoundary, this.springApplicationName, this.testObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.adminEmail))
				.isInstanceOf(HttpClientErrorException.class)
				.extracting("statusCode.value")
				.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
}
