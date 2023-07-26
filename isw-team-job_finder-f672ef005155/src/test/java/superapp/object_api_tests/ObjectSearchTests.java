package superapp.object_api_tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.NewUserBoundary;
import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.UserBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectCreatorBoundary;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ObjectSearchTests {
    private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	private String springApplicationName;
	private String testMiniappUserEmail; 
	private String testSuperappUserEmail;
	private SuperAppObjectBoundary testObject;
	
	
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
		this.testSuperappUserEmail = "superapp@demo.org";
		this.testMiniappUserEmail = "miniapp@demo.org";
	}

	@BeforeEach
	public void resetState() {
		// Tear down DB:
		tearDown();
		
		// A valid NewUserBoundary with the SUPERAPP_USER role in order to create 
		// and search active / not active super app objects
		NewUserBoundary newSuperappUser = new NewUserBoundary();
		
		newSuperappUser.setEmail(this.testSuperappUserEmail);
		newSuperappUser.setUsername("Superapp Smith");
		newSuperappUser.setAvatar("S");
		newSuperappUser.setRole("SUPERAPP_USER");
		
		//Add superapp user to database
		UserBoundary superappUser =  this.restTemplate.postForObject(this.baseUrl + "/users", 
				newSuperappUser, UserBoundary.class);
		
		
		// A valid NewUserBoundary with the MINIAPP_USER role in order to create 
		// and search active / not active super app objects 
		NewUserBoundary newMiniappUser = new NewUserBoundary();
		
		newMiniappUser.setEmail(this.testMiniappUserEmail);
		newMiniappUser.setUsername("Miniapp Mini");
		newMiniappUser.setAvatar("M");
		newMiniappUser.setRole("MINIAPP_USER");
		
		//Add miniapp user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", 
				newMiniappUser, UserBoundary.class);
		
		
		// A valid SuperAppObjectBoundary, as the instance to search
		SuperAppObjectBoundary testSuperAppObjectBoundary = new  SuperAppObjectBoundary();
		
		testSuperAppObjectBoundary.setAlias("I am testing!");
		testSuperAppObjectBoundary.setType("TestObject");
		
		
		SuperAppObjectCreatorBoundary createdBy = new SuperAppObjectCreatorBoundary();
		createdBy.setUserId(superappUser.getUserId());
		testSuperAppObjectBoundary.setCreatedBy(createdBy);
		
		// add SuperAppObjectBoundary to database
		this.restTemplate.postForObject(this.baseUrl + "/objects", 
				testSuperAppObjectBoundary, SuperAppObjectBoundary.class);
		
		// get the added object from the database
		this.testObject = this.restTemplate.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.testSuperappUserEmail)[0];
		
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
	
	
	/////////////////////////////////////////////
	//		SEARCH BY TYPE
	/////////////////////////////////////////////
	@Test
	@DisplayName("Test search by type with SUPERAPP_USER and active=False")
	public void testSearchByTypeWithSuperappUserNotActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND type="TestObject"
		// AND active=False
		
		// WHEN I GET /superapp/objects/search/byType/TestObject?userSuperapp=2023b.ido.ronen&userEmail=superapp@demo.org&size=20&page=0
		// (search for the object with superapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byType/{type}?userSuperapp={userSuperapp}&userEmail={userEmail}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getType(),
						this.springApplicationName,
						this.testSuperappUserEmail);
		
		// THEN the server returns a list of SuperAppObjectBoundary of size 1 
		// AND the object in the list in equal to this.testObject
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.hasSize(1)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);	
	}
	
	
	@Test
	@DisplayName("Test search by type with SUPERAPP_USER and active=True")
	public void testSearchByTypeWithSuperappUserActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND type="TestObject"
		// AND active=True
		this.testObject.setActive(true);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
					this.testObject,
					this.springApplicationName,
					this.testObject.getObjectId().getInternalObjectId(),
					this.springApplicationName,
					this.testSuperappUserEmail);
		
		
		// WHEN I GET /superapp/objects/search/byType/TestObject?userSuperapp=2023b.ido.ronen&userEmail=superapp@demo.org&size=20&page=0
		// (search for the object with superapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byType/{type}?userSuperapp={userSuperapp}&userEmail={userEmail}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getType(),
						this.springApplicationName,
						this.testSuperappUserEmail);
		
		// THEN the server returns a list of SuperAppObjectBoundary of size 1 
		// AND the object in the list in equal to this.testObject
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.hasSize(1)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);	
	}
	
	
	@Test
	@DisplayName("Test search by type with MINIAPP_USER and active=False")
	public void testSearchByTypeWithMiniappUserNotActiveFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND type="TestObject"
		// AND active=False
		
		// WHEN I GET /superapp/objects/search/byType/TestObject?userSuperapp=2023b.ido.ronen&userEmail=miniapp@demo.org&size=20&page=0
		// (search for the object with miniapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byType/{type}?userSuperapp={userSuperapp}&userEmail={userEmail}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getType(),
						this.springApplicationName,
						this.testMiniappUserEmail);
		
		// THEN the server returns an empty list = []
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.isEmpty();
	}
	
	

	@Test
	@DisplayName("Test search by type with MINIAPP_USER and active=True")
	public void testSearchByTypeWithMiniappUserActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND type="TestObject"
		// AND active=True
		this.testObject.setActive(true);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
					this.testObject,
					this.springApplicationName,
					this.testObject.getObjectId().getInternalObjectId(),
					this.springApplicationName,
					this.testSuperappUserEmail);
		
		
		// WHEN I GET /superapp/objects/search/byType/TestObject?userSuperapp=2023b.ido.ronen&userEmail=miniapp@demo.org&size=20&page=0
		// (search for the object with miniapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byType/{type}?userSuperapp={userSuperapp}&userEmail={userEmail}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getType(),
						this.springApplicationName,
						this.testMiniappUserEmail);
		
		// THEN the server returns a list of SuperAppObjectBoundary of size 1 
		// AND the object in the list in equal to this.testObject
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.hasSize(1)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);	
	}
	
	
	/////////////////////////////////////////////
	//		SEARCH BY ALIAS
	/////////////////////////////////////////////
	@Test
	@DisplayName("Test search by alias with SUPERAPP_USER and active=False")
	public void testSearchByAliasWithSuperappUserNotActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND alias="I am testing!"
		// AND active=False
		
		// WHEN I GET /superapp/objects/search/byAlias/I am testing!?userSuperapp=2023b.ido.ronen&userEmail=superapp@demo.org&size=20&page=0
		// (search for the object with superapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byAlias/{alias}?userSuperapp={userSuperapp}&userEmail={userEmail}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getAlias(),
						this.springApplicationName,
						this.testSuperappUserEmail);
		
		// THEN the server returns a list of SuperAppObjectBoundary of size 1 
		// AND the object in the list in equal to this.testObject
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.hasSize(1)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);	
	}
	
	
	@Test
	@DisplayName("Test search by alias with SUPERAPP_USER and active=True")
	public void testSearchByAliasWithSuperappUserActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND alias="I am testing!"
		// AND active=True
		this.testObject.setActive(true);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
					this.testObject,
					this.springApplicationName,
					this.testObject.getObjectId().getInternalObjectId(),
					this.springApplicationName,
					this.testSuperappUserEmail);
		
		
		// WHEN I GET /superapp/objects/search/byAlias/I am testing!?userSuperapp=2023b.ido.ronen&userEmail=superapp@demo.org&size=20&page=0
		// (search for the object with superapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byAlias/{alias}?userSuperapp={userSuperapp}&userEmail={userEmail}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getAlias(),
						this.springApplicationName,
						this.testSuperappUserEmail);
		
		// THEN the server returns a list of SuperAppObjectBoundary of size 1 
		// AND the object in the list in equal to this.testObject
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.hasSize(1)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);	
	}

	
	@Test
	@DisplayName("Test search by alias with MINIAPP_USER and active=False")
	public void testSearchByAliasWithMiniappUserNotActiveFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND alias="I am testing!"
		// AND active=False
		
		// WHEN I GET /superapp/objects/search/byAlias/I am testing!?userSuperapp=2023b.ido.ronen&userEmail=miniapp@demo.org&size=20&page=0
		// (search for the object with mini user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byAlias/{alias}?userSuperapp={userSuperapp}&userEmail={userEmail}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getAlias(),
						this.springApplicationName,
						this.testMiniappUserEmail);
		
		// THEN the server returns an empty list = []
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.isEmpty();
	}

	
	@Test
	@DisplayName("Test search by alias with MINIAPP_USER and active=True")
	public void testSearchByAliasWithMiniappUserActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND alias="I am testing!"
		// AND active=True
		this.testObject.setActive(true);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
					this.testObject,
					this.springApplicationName,
					this.testObject.getObjectId().getInternalObjectId(),
					this.springApplicationName,
					this.testSuperappUserEmail);
		
		
		// WHEN I GET /superapp/objects/search/byAlias/I am testing!?userSuperapp=2023b.ido.ronen&userEmail=miniapp@demo.org&size=20&page=0
		// (search for the object with miniapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byAlias/{alias}?userSuperapp={userSuperapp}&userEmail={userEmail}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getAlias(),
						this.springApplicationName,
						this.testMiniappUserEmail);
		
		// THEN the server returns a list of SuperAppObjectBoundary of size 1 
		// AND the object in the list in equal to this.testObject
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.hasSize(1)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);	
	}
	
	
	/////////////////////////////////////////////
	//		SEARCH BY LOCATION 
	/////////////////////////////////////////////
	@Test
	@DisplayName("Test search by location with SUPERAPP_USER, and active=False")
	public void testSearchByLocationWithSuperappUserNotActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND lat = 0.0, lng = 0.0
		// AND metric=NEUTRAL
		// AND active=False
		String distanceUnits = "NEUTRAL";
		double distance = 1;
		int size = 1;
		int page = 0;
		
		// WHEN I GET /superapp/objects/search/byLocation/0.0/0.0/1?userSuperapp=2023b.ido.ronen&userEmail=superapp@demo.org&size=1&page=0
		// (search for the object with superapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byLocation/{lat}/{lng}/{distance}?distanceUnits={distanceUnits}&"
						+ "userSuperapp={userSuperapp}&userEmail={userEmail}&size={size}&page={page}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getLocation().getLat(),
						this.testObject.getLocation().getLng(),
						distance,
						distanceUnits,
						this.springApplicationName,
						this.testSuperappUserEmail,
						size,
						page);
		
		// THEN the server returns a list of SuperAppObjectBoundary of size 1 
		// AND the object in the list in equal to this.testObject
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.hasSize(1)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);	
	}
	
	
	@Test
	@DisplayName("Test search by location with SUPERAPP_USER, and active=True")
	public void testSearchByLocationWithSuperappUserActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND lat = 0.0, lng = 0.0
		// AND metric=NEUTRAL
		// AND active=False
		String distanceUnits = "NEUTRAL";
		double distance = 1;
		int size = 1;
		int page = 0;
		this.testObject.setActive(true);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
					this.testObject,
					this.springApplicationName,
					this.testObject.getObjectId().getInternalObjectId(),
					this.springApplicationName,
					this.testSuperappUserEmail);
		
		// WHEN I GET /superapp/objects/search/byLocation/0.0/0.0/1?userSuperapp=2023b.ido.ronen&userEmail=superapp@demo.org&size=1&page=0
		// (search for the object with superapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byLocation/{lat}/{lng}/{distance}?distanceUnits={distanceUnits}&"
						+ "userSuperapp={userSuperapp}&userEmail={userEmail}&size={size}&page={page}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getLocation().getLat(),
						this.testObject.getLocation().getLng(),
						distance,
						distanceUnits,
						this.springApplicationName,
						this.testSuperappUserEmail,
						size,
						page);
		
		// THEN the server returns a list of SuperAppObjectBoundary of size 1 
		// AND the object in the list in equal to this.testObject
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.hasSize(1)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);	
	}
	
	
	@Test
	@DisplayName("Test search by location with MINIAPP_USER, and active=False")
	public void testSearchByLocationWithMiniappUserNotActiveFails() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND lat = 0.0, lng = 0.0
		// AND metric=NEUTRAL
		// AND active=False
		String distanceUnits = "NEUTRAL";
		double distance = 1;
		int size = 1;
		int page = 0;
		
		// WHEN I GET /superapp/objects/search/byLocation/0.0/0.0/1?userSuperapp=2023b.ido.ronen&userEmail=superapp@demo.org&size=1&page=0
		// (search for the object with miniapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byLocation/{lat}/{lng}/{distance}?distanceUnits={distanceUnits}&"
						+ "userSuperapp={userSuperapp}&userEmail={userEmail}&size={size}&page={page}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getLocation().getLat(),
						this.testObject.getLocation().getLng(),
						distance,
						distanceUnits,
						this.springApplicationName,
						this.testMiniappUserEmail,
						size,
						page);
		
		// THEN the server returns an empty list = []
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.isEmpty();
	}
	
	
	@Test
	@DisplayName("Test search by location with MINIAPP_USER, and active=False")
	public void testSearchByLocationWithMiniappUserActiveSucceeds() {
		// GIVEN the server is up
		// AND the SUPERAPPOBJECTS collection contains 1 object (testObject)
		// AND lat = 0.0, lng = 0.0
		// AND metric=NEUTRAL
		// AND active=False
		String distanceUnits = "NEUTRAL";
		double distance = 1;
		int size = 1;
		int page = 0;
		this.testObject.setActive(true);
		
		this.restTemplate
			.put(this.baseUrl + "/objects/{superapp}/{internalObjectId}?userSuperapp={userSuperapp}&userEmail={userEmail}",
					this.testObject,
					this.springApplicationName,
					this.testObject.getObjectId().getInternalObjectId(),
					this.springApplicationName,
					this.testSuperappUserEmail);
		
		// WHEN I GET /superapp/objects/search/byLocation/0.0/0.0/1?userSuperapp=2023b.ido.ronen&userEmail=superapp@demo.org&size=1&page=0
		// (search for the object with miniapp user)
		SuperAppObjectBoundary[] retreivedObjectBoundary = this.restTemplate
				.getForObject(this.baseUrl + 
						"/objects/search/byLocation/{lat}/{lng}/{distance}?distanceUnits={distanceUnits}&"
						+ "userSuperapp={userSuperapp}&userEmail={userEmail}&size={size}&page={page}", 
						SuperAppObjectBoundary[].class,
						this.testObject.getLocation().getLat(),
						this.testObject.getLocation().getLng(),
						distance,
						distanceUnits,
						this.springApplicationName,
						this.testMiniappUserEmail,
						size,
						page);
		
		// THEN the server returns a list of SuperAppObjectBoundary of size 1 
		// AND the object in the list in equal to this.testObject
		assertThat(retreivedObjectBoundary)
			.isNotNull()
			.hasSize(1)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testObject);	
	}
	
}
