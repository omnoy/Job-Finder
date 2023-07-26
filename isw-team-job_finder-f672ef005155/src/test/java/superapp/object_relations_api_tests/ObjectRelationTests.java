package superapp.object_relations_api_tests;

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
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;
import superapp.boundaries.sub_boundaries.UserIdBoundary;

/** This test-case contains tests regarding the Object Relation Creation and Retrieval features.
 * 
 * @author Rom Gat
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ObjectRelationTests {
	private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private String springApplicationName;
	private SuperAppObjectBoundary testObjectInDB1;
	private SuperAppObjectBoundary testObjectInDB2;
	
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
		
		// Create two valid object boundary, POST them and GET them using their ids
		SuperAppObjectBoundary objectToPost = new SuperAppObjectBoundary();
		
		objectToPost.setAlias("I am testing!");
		objectToPost.setType("TestObject");
		
		UserIdBoundary createdBy = new UserIdBoundary();
		createdBy.setSuperapp(this.springApplicationName);
		createdBy.setEmail(this.superappUserEmail);
		
		SuperAppObjectCreatorBoundary objectCreator = new SuperAppObjectCreatorBoundary();
		objectCreator.setUserId(createdBy);
		
		objectToPost.setCreatedBy(objectCreator);
		
		SuperAppObjectBoundary returnedObject = this.restTemplate
				.postForObject(this.baseUrl + "/objects", objectToPost, SuperAppObjectBoundary.class);

		this.testObjectInDB1 = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary.class, this.springApplicationName, returnedObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		objectToPost.getCreatedBy().getUserId().setEmail(this.superappUserEmail);
		
		returnedObject = this.restTemplate
				.postForObject(this.baseUrl + "/objects", objectToPost, SuperAppObjectBoundary.class);

		this.testObjectInDB2 = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary.class, this.springApplicationName, returnedObject.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
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
		
		this.restTemplate.delete(this.baseUrl + "/admin/objects?userSuperapp={userSuperapp}&userEmail={email}",
				this.springApplicationName, tearDownAdminEmail);
		this.restTemplate.delete(this.baseUrl + "/admin/miniapp?userSuperapp={userSuperapp}&userEmail={email}",
				this.springApplicationName, tearDownAdminEmail);
		//delete users last since it contains the admin user
		this.restTemplate.delete(this.baseUrl + "/admin/users?userSuperapp={userSuperapp}&userEmail={email}",
				this.springApplicationName, tearDownAdminEmail);
	}
	
	
	@Test
	@DisplayName("Test object binding of two existing objects succeeds for parent")
	public void testObjectBindingOfTwoExistingObjectsSucceedsForParent() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com with
		/*	{
		 * 		"superapp": "2023b.ido.ronen",
		 * 		"internalObjectId": <IOD2>
		 * 	}*/
		// (<IOD1> and <IOD2> are internal object ids of testObjectInDB1 and testObjectInDB2)
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
						this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the set of children of testObjectInDB1 contains a single object with (But not limited to)
		/*	{
		 * 		"objectId": {
		 * 			"superapp": "2023b.ido.ronen",
		 * 			"internalObjectId": <IOD2>
		 * 		}
		 * 	}*/
		// (which is the new version from DB of testObjectInDB2)
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{object}?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary.class, this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
							this.springApplicationName, this.superappUserEmail));
	}
	
	@Test
	@DisplayName("Test object binding of two existing objects succeeds for child")
	public void testObjectBindingOfTwoExistingObjectsSucceedsForChild() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com with
		/*	{
		 * 		"superapp": "2023b.ido.ronen",
		 * 		"internalObjectId": <IOD2>
		 * 	}*/
		// (<IOD1> and <IOD2> are internal object ids of testObjectInDB1 and testObjectInDB2)
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
						this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the set of parents of testObjectInDB2 contains a single object with (But not limited to)
		/*	{
		 * 		"objectId": {
		 * 			"superapp": "2023b.ido.ronen",
		 * 			"internalObjectId": <IOD1>
		 * 		}
		 * 	}*/
		// (which is the new version from DB of testObjectInDB1)
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary.class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, this.superappUserEmail));
	}
	
	@Test
	@DisplayName("Test object binding of two bound objects does not affect parent's set of children")
	public void testObjectBindingOfTwoBoundObjectsDoesNotAffectParentsSetOfChildren() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two bound objects (testObjectInDB1, testObjectInDB2)
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);
		
		SuperAppObjectBoundary[] children = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com with
		/*	{
		 * 		"superapp": "2023b.ido.ronen",
		 * 		"internalObjectId": <IOD2>
		 * 	}*/
		// (<IOD1> and <IOD2> are internal object ids of testObjectInDB1 and testObjectInDB2)
		// (this means binding them again in the same manner)
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
						this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the set of children of testObjectInDB1 does not change
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.isEqualTo(children);
	}
	
	@Test
	@DisplayName("Test object binding of two bound objects does not affect child's set of parents")
	public void testObjectBindingOfTwoBoundObjectsDoesNotAffectChildsSetOfParents() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two bound objects (testObjectInDB1, testObjectInDB2)
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);
		
		SuperAppObjectBoundary[] parents = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com with
		/*	{
		 * 		"superapp": "2023b.ido.ronen",
		 * 		"internalObjectId": <IOD2>
		 * 	}*/
		// (<IOD1> and <IOD2> are internal object ids of testObjectInDB1 and testObjectInDB2)
		// (this means binding them again in the same manner)
		this.restTemplate
				.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
						this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		// THEN the set of parents of testObjectInDB2 does not change
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail))
			.usingRecursiveFieldByFieldElementComparator()
			.isEqualTo(parents);
	}
	
	@Test
	@DisplayName("Test object binding without any existing objectIds fails")
	public void testObjectBindingWithoutAnyExistingObjectIdsFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I PUT /superapp/objects/notOurAppName/<nonExistingId>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com with {}
		// (<nonExistingId> is a non-existing internal object id)
		// THEN the server responds with 404 - Not Found
		SuperAppObjectIdBoundary invalidObjectId = new SuperAppObjectIdBoundary();
		String nonExistingId = this.testObjectInDB1.getObjectId().getInternalObjectId()
				+ this.testObjectInDB2.getObjectId().getInternalObjectId()
				+ "extensionWhichRuinsThemAll";
		
		assertThatThrownBy(()->
				this.restTemplate
					.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							invalidObjectId, "notOurAppName", nonExistingId, this.springApplicationName, this.superappUserEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	@DisplayName("Test object binding without existing parent's objectId fails")
	public void testObjectBindingWithoutExistingParentsObjectIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I PUT /superapp/objects/notOurAppName/<nonExistingId>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com with
		/*	{
		 * 		"superapp": "2023b.ido.ronen",
		 * 		"internalObjectId": <IOD2>
		 * 	}*/
		// (<nonExistingId> is a non-existing internal object id)
		// (<IOD2> is internal object id of testObjectInDB2)
		// THEN the server responds with 404 - Not Found
		String nonExistingId = this.testObjectInDB1.getObjectId().getInternalObjectId()
				+ this.testObjectInDB2.getObjectId().getInternalObjectId()
				+ "extensionWhichRuinsThemAll";
		
		assertThatThrownBy(()->
				this.restTemplate
					.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							this.testObjectInDB2.getObjectId(), "notOurAppName", nonExistingId, this.springApplicationName, this.superappUserEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	@DisplayName("Test object binding without existing child's objectId fails")
	public void testObjectBindingWithoutExistingChildsObjectIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com with {}
		// (<IOD1> is internal object id of testObjectInDB1)
		// THEN the server responds with 404 - Not Found
		SuperAppObjectIdBoundary invalidObjectId = new SuperAppObjectIdBoundary();
		
		assertThatThrownBy(()->
				this.restTemplate
					.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							invalidObjectId, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, this.superappUserEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	@DisplayName("Test object's children retrieval without existing objectId fails")
	public void testObjectsChildrenRetrievalWithoutExistingObjectIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I GET /superapp/objects/notOurAppName/<nonExistingId>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com
		// (<nonExistingId> is a non-existing internal object id)
		// THEN the server responds with 404 - Not Found
		String nonExistingId = this.testObjectInDB1.getObjectId().getInternalObjectId()
				+ this.testObjectInDB2.getObjectId().getInternalObjectId()
				+ "extensionWhichRuinsThemAll";
		
		assertThatThrownBy(()->
				this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary[].class, "notOurAppName", nonExistingId, this.springApplicationName, this.superappUserEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	@DisplayName("Test object's parents retrieval without existing objectId fails")
	public void testObjectsParentsRetrievalWithoutExistingObjectIdFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I GET /superapp/objects/notOurAppName/<nonExistingId>/parents?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com
		// (<nonExistingId> is a non-existing internal object id)
		// THEN the server responds with 404 - Not Found
		String nonExistingId = this.testObjectInDB1.getObjectId().getInternalObjectId()
				+ this.testObjectInDB2.getObjectId().getInternalObjectId()
				+ "extensionWhichRuinsThemAll";
		
		assertThatThrownBy(()->
				this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary[].class, "notOurAppName", nonExistingId, this.springApplicationName, this.superappUserEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	@DisplayName("Test object binding of two existing objects by non-existing user fails")
	public void testObjectBindingOfTwoExistingObjectsByNonExistingUserFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=nonExistingUser@gmail.com with
		/*	{
		 * 		"superapp": "2023b.ido.ronen",
		 * 		"internalObjectId": <IOD2>
		 * 	}*/
		// (<IOD1> and <IOD2> are internal object ids of testObjectInDB1 and testObjectInDB2)
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(()->
				this.restTemplate
					.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, "nonExistingUser@gmail.com"))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object binding of two existing objects by miniapp user fails")
	public void testObjectBindingOfTwoExistingObjectsByMiniappUserFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=miniappUser@gmail.com with
		/*	{
		 * 		"superapp": "2023b.ido.ronen",
		 * 		"internalObjectId": <IOD2>
		 * 	}*/
		// (<IOD1> and <IOD2> are internal object ids of testObjectInDB1 and testObjectInDB2)
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(()->
				this.restTemplate
					.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, this.miniappUserEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object binding of two existing objects by admin fails")
	public void testObjectBindingOfTwoExistingObjectsByAdminFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I PUT /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=admin@gmail.com with
		/*	{
		 * 		"superapp": "2023b.ido.ronen",
		 * 		"internalObjectId": <IOD2>
		 * 	}*/
		// (<IOD1> and <IOD2> are internal object ids of testObjectInDB1 and testObjectInDB2)
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(()->
				this.restTemplate
					.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, this.adminEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object's children retrieval by non-existing user fails")
	public void testObjectsChildrenRetrievalByNonExistingUserFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=nonExistingUser@gmail.com
		// (<IOD1> is internal object id of testObjectInDB1)
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(()->
				this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, "nonExistingUser@gmail.com"))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object's parents retrieval by non-existing user fails")
	public void testObjectsParentsRetrievalByNonExistingUserFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/parents?userSuperapp=2023b.ido.ronen&userEmail=nonExistingUser@gmail.com
		// (<IOD1> is internal object id of testObjectInDB1)
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(()->
				this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, "nonExistingUser@gmail.com"))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object's children retrieval by admin fails")
	public void testObjectsChildrenRetrievalByAdminFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=admin@gmail.com
		// (<IOD1> is internal object id of testObjectInDB1)
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(()->
				this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, this.adminEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object's parents retrieval by admin fails")
	public void testObjectsParentsRetrievalByAdminFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/parents?userSuperapp=2023b.ido.ronen&userEmail=admin@gmail.com
		// (<IOD1> is internal object id of testObjectInDB1)
		// THEN the server responds with 403 - Forbidden
		assertThatThrownBy(()->
				this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, this.adminEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test inactive object's children retrieval by miniapp user fails")
	public void testInactiveObjectsChildrenRetrievalByMiniappUserFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is inactive
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=miniappUser@gmail.com
		// (<IOD1> is internal object id of testObjectInDB1)
		// THEN the server responds with 404 - Not Found
		assertThatThrownBy(()->
				this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, this.miniappUserEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	@DisplayName("Test inactive object's parents retrieval by miniapp user fails")
	public void testInactiveObjectsParentsRetrievalByMiniappUserFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is inactive
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/parents?userSuperapp=2023b.ido.ronen&userEmail=miniappUser@gmail.com
		// (<IOD1> is internal object id of testObjectInDB1)
		// THEN the server responds with 404 - Not Found
		assertThatThrownBy(()->
				this.restTemplate
					.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}",
							SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
							this.springApplicationName, this.miniappUserEmail))
					.isInstanceOf(HttpClientErrorException.class)
					.extracting("statusCode.value")
					.isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	@DisplayName("Test active object's children retrieval by miniapp user returns only active objects")
	public void testActiveObjectsChildrenRetrievalByMiniappUserReturnsOnlyActiveObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is active
		// AND testObjectInDB2 is inactive
		// AND testObjectInDB1 is parent of testObjectInDB2
		this.testObjectInDB1.setActive(true);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB1, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);
		
		this.testObjectInDB1 = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary.class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=miniappUser@gmail.com
		// (<IOD1> is internal object id of testObjectInDB1)
		SuperAppObjectBoundary[] children = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.miniappUserEmail);
		
		// THEN the server returns an empty array (Not including the bound child)
		assertThat(children).isEmpty();
	}
	
	@Test
	@DisplayName("Test active object's parents retrieval by miniapp user returns only active objects")
	public void testActiveObjectsParentsRetrievalByMiniappUserReturnsOnlyActiveObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is inactive
		// AND testObjectInDB2 is active
		// AND testObjectInDB1 is parent of testObjectInDB2
		this.testObjectInDB2.setActive(true);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2, this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);
		
		this.testObjectInDB2 = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary.class, this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.superappUserEmail);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);
		
		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD2>/parents?userSuperapp=2023b.ido.ronen&userEmail=miniappUser@gmail.com
		// (<IOD2> is internal object id of testObjectInDB2)
		SuperAppObjectBoundary[] parents = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}",
						SuperAppObjectBoundary[].class, this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
						this.springApplicationName, this.miniappUserEmail);
		
		// THEN the server returns an empty array (Not including the bound child)
		assertThat(parents).isEmpty();
	}
	
	@Test
	@DisplayName("Test object's children retrieval with first large page returns all objects")
	public void testObjectsChildrenRetrievalWithFirstLargePageReturnsAllObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is parent of testObjectInDB1
		// AND testObjectInDB1 is parent of testObjectInDB2
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB1.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com&size=3&page=0
		// (<IOD1> is internal object id of testObjectInDB1)
		SuperAppObjectBoundary[] children = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}"
						+ "&size={size}&page={page}", SuperAppObjectBoundary[].class, this.springApplicationName,
						this.testObjectInDB1.getObjectId().getInternalObjectId(), this.springApplicationName, this.superappUserEmail, 3, 0);
		
		// THEN the server returns both objects, in reverse-posting order
		assertThat(children)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObjectInDB2, this.testObjectInDB1);
	}
	
	@Test
	@DisplayName("Test object's children retrieval with first small page returns only some objects")
	public void testObjectsChildrenRetrievalWithFirstSmallPageReturnsOnlySomeObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is parent of testObjectInDB1
		// AND testObjectInDB1 is parent of testObjectInDB2
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB1.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com&size=1&page=0
		// (<IOD1> is internal object id of testObjectInDB1)
		SuperAppObjectBoundary[] children = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}"
						+ "&size={size}&page={page}", SuperAppObjectBoundary[].class, this.springApplicationName,
						this.testObjectInDB1.getObjectId().getInternalObjectId(), this.springApplicationName, this.superappUserEmail, 1, 0);
		
		// THEN the server returns only one object (last one posted)
		assertThat(children)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObjectInDB2);
	}
	
	@Test
	@DisplayName("Test object's children retrieval with second small page returns only some objects")
	public void testObjectsChildrenRetrievalWithSecondSmallPageReturnsOnlySomeObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is parent of testObjectInDB1
		// AND testObjectInDB1 is parent of testObjectInDB2
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB1.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD1>/children?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com&size=1&page=1
		// (<IOD1> is internal object id of testObjectInDB1)
		SuperAppObjectBoundary[] children = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}"
						+ "&size={size}&page={page}", SuperAppObjectBoundary[].class, this.springApplicationName,
						this.testObjectInDB1.getObjectId().getInternalObjectId(), this.springApplicationName, this.superappUserEmail, 1, 1);
		
		// THEN the server returns only one object (first one posted)
		assertThat(children)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObjectInDB1);
	}
	
	@Test
	@DisplayName("Test object's parents retrieval with first large page returns all objects")
	public void testObjectsParentsRetrievalWithFirstLargePageReturnsAllObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is parent of testObjectInDB2
		// AND testObjectInDB2 is parent of testObjectInDB2
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD2>/parents?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com&size=3&page=0
		// (<IOD2> is internal object id of testObjectInDB2)
		SuperAppObjectBoundary[] parents = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}"
						+ "&size={size}&page={page}", SuperAppObjectBoundary[].class, this.springApplicationName,
						this.testObjectInDB2.getObjectId().getInternalObjectId(), this.springApplicationName, this.superappUserEmail, 3, 0);
		
		// THEN the server returns both objects, in reverse-posting order
		assertThat(parents)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObjectInDB2, this.testObjectInDB1);
	}
	
	@Test
	@DisplayName("Test object's parents retrieval with first small page returns only some objects")
	public void testObjectsParentsRetrievalWithFirstSmallPageReturnsOnlySomeObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is parent of testObjectInDB2
		// AND testObjectInDB2 is parent of testObjectInDB2
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD2>/parents?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com&size=1&page=0
		// (<IOD2> is internal object id of testObjectInDB2)
		SuperAppObjectBoundary[] parents = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}"
						+ "&size={size}&page={page}", SuperAppObjectBoundary[].class, this.springApplicationName,
						this.testObjectInDB2.getObjectId().getInternalObjectId(), this.springApplicationName, this.superappUserEmail, 1, 0);
		
		// THEN the server returns only one object (last one posted)
		assertThat(parents)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObjectInDB2);
	}
	
	@Test
	@DisplayName("Test object's parents retrieval with second small page returns only some objects")
	public void testObjectsParentsRetrievalWithSecondSmallPageReturnsOnlySomeObjects() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains two objects (testObjectInDB1, testObjectInDB2)
		// AND testObjectInDB1 is parent of testObjectInDB2
		// AND testObjectInDB2 is parent of testObjectInDB2
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB1.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}/children?userSuperapp={userSuperapp}&userEmail={email}",
					this.testObjectInDB2.getObjectId(), this.springApplicationName, this.testObjectInDB2.getObjectId().getInternalObjectId(),
					this.springApplicationName, this.superappUserEmail);

		// WHEN I GET /superapp/objects/2023b.ido.ronen/<IOD2>/parents?userSuperapp=2023b.ido.ronen&userEmail=superappUser@gmail.com&size=1&page=1
		// (<IOD2> is internal object id of testObjectInDB2)
		SuperAppObjectBoundary[] parents = this.restTemplate
				.getForObject(this.baseUrl + "/objects/{superapp}/{internalObjectId}/parents?userSuperapp={userSuperapp}&userEmail={email}"
						+ "&size={size}&page={page}", SuperAppObjectBoundary[].class, this.springApplicationName,
						this.testObjectInDB2.getObjectId().getInternalObjectId(), this.springApplicationName, this.superappUserEmail, 1, 1);
		
		// THEN the server returns only one object (first one posted)
		assertThat(parents)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObjectInDB1);
	}
	
}
