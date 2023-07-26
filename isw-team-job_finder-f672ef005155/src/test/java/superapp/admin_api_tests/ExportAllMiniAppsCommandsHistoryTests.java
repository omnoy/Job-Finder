package superapp.admin_api_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Date;

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
import superapp.boundaries.MiniAppCommandBoundary;
import superapp.boundaries.NewUserBoundary;
import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.UserBoundary;
import superapp.boundaries.sub_boundaries.CommandInvokerBoundary;
import superapp.boundaries.sub_boundaries.MiniAppCommandIdBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectCreatorBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectTargeterBoundary;

/** This test-case contains tests regarding the User Creation feature.
 * 
 * @author Ido Ronen
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ExportAllMiniAppsCommandsHistoryTests {
    private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private String springApplicationName;
	private String miniAppName;
	private String testAdminUserEmail; 
	
	private UserBoundary testSuperappUser;
	private String testSuperappUserEmail;
	
	private UserBoundary testMiniappUser;
	private String testMiniappUserEmail;
	
	private SuperAppObjectBoundary testObject;
	private MiniAppCommandBoundary testCommand;
	
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
		this.miniAppName = "testMiniApp";
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
		
		//Add new user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", newTestAdminUser, UserBoundary.class);
		
		// A valid NewUserBoundary with the SUPERAPP_USER role in order to export all objects
		// SuperApp User Email for authorization
		this.testSuperappUserEmail = "superapp@demo.org";
		
		// A valid NewUserBoundary with the SUPERAPP_USER role in order to create objects
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
		
		// A valid NewUserBoundary with the MINIAPP_USER role in order to invoke miniapp commands
		// SuperApp User Email for authorization
		this.testMiniappUserEmail = "miniapp@demo.org";
		
		// A valid NewUserBoundary with the MINIAPP_USER role in order to perform tests
		NewUserBoundary newTestMiniappUser = new NewUserBoundary();
		
		newTestMiniappUser.setEmail(this.testMiniappUserEmail);
		newTestMiniappUser.setUsername("Miniapp Smith");
		newTestMiniappUser.setAvatar("M");
		newTestMiniappUser.setRole("MINIAPP_USER");
		
		//Add new user to database
		this.restTemplate.postForObject(this.baseUrl + "/users", newTestMiniappUser, UserBoundary.class);
		
		//Also get user from database 
		this.testMiniappUser = this.restTemplate
				.getForObject(this.baseUrl + "/users/login/{superapp}/{email}", 
						UserBoundary.class, this.springApplicationName, this.testMiniappUserEmail);
		
		// A valid SuperAppObjectBoundary for miniapp target
		SuperAppObjectBoundary testSuperAppObjectBoundary = new  SuperAppObjectBoundary();
		
		testSuperAppObjectBoundary.setAlias("I am testing!");
		testSuperAppObjectBoundary.setType("TestObject");
		testSuperAppObjectBoundary.setActive(true);
		
		SuperAppObjectCreatorBoundary createdBy = new SuperAppObjectCreatorBoundary();
		createdBy.setUserId(this.testSuperappUser.getUserId());
		testSuperAppObjectBoundary.setCreatedBy(createdBy);
		
		// add SuperAppObjectBoundary to database
		this.restTemplate.postForObject(this.baseUrl + "/objects", 
				testSuperAppObjectBoundary, SuperAppObjectBoundary.class);
		
		// get the added object from the database
		this.testObject = this.restTemplate.getForObject(this.baseUrl + "/objects?userSuperapp={userSuperapp}&userEmail={userEmail}", SuperAppObjectBoundary[].class,
						this.springApplicationName, this.testSuperappUserEmail)[0];
		
		//create miniapp command for posting
		this.testCommand = new MiniAppCommandBoundary();
		
		this.testCommand.setCommand("I'm a test");
		
		MiniAppCommandIdBoundary commandIdBoundary = new MiniAppCommandIdBoundary();
		commandIdBoundary.setSuperapp(this.springApplicationName);
		commandIdBoundary.setMiniapp(this.miniAppName);
		commandIdBoundary.setInternalCommandId("1");
		this.testCommand.setCommandId(commandIdBoundary);
		
		SuperAppObjectTargeterBoundary targetBoundary = new SuperAppObjectTargeterBoundary();
		targetBoundary.setObjectId(this.testObject.getObjectId());
		this.testCommand.setTargetObject(targetBoundary);
		
		this.testCommand.setInvocationTimestamp(new Date());
		
		CommandInvokerBoundary invokerByBoundary = new CommandInvokerBoundary();
		invokerByBoundary.setUserId(this.testMiniappUser.getUserId());
		this.testCommand.setInvokedBy(invokerByBoundary);		
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
	@DisplayName("Test export all command history with user id not found fails")
	public void testExportAllCommandHistoryWithUserIdNotFoundFails() {
		// GIVEN the server is up
		// AND the USERS collection does not contain a user with the email "jill@demo.org"
		
		// WHEN I GET /superapp/admin/users?userSuperapp=2023b.ido.ronen&userEmail=jill@demo.org
		
		// THEN the server responds with 403 - FORBIDDEN
		
		assertThatThrownBy(()->
		this.restTemplate.getForObject(this.baseUrl + "/admin/miniapp?userSuperapp={superapp}&userEmail={email}", 
				MiniAppCommandBoundary[].class,
				this.springApplicationName, "jill@demo.org"))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value());
		
	}
	
	@Test
	@DisplayName("Test export all command history with unauthorized user role fails")
	public void testExportAllCommandHistoryWithUnauthorizedUserRoleFails() {
		// GIVEN the server is up
		// AND the USERS collection contains a user containing the following fields:
		//	{
		//		"userId": {
		//			"superapp": "2023b.ido.ronen",
		//			"email": "jill@demo.org"
		//		},
		//		"role":"MINIAPP_USER"
		//	}
		
		NewUserBoundary testUser = new NewUserBoundary();
		String testUserEmail = "jill@demo.org";
		testUser.setUsername("Jill");
		testUser.setEmail(testUserEmail);
		testUser.setRole("MINIAPP_USER");
		testUser.setAvatar("J");
		this.restTemplate.postForObject(this.baseUrl + "/users", testUser, UserBoundary.class);
		
		// WHEN I GET /superapp/admin/users?userSuperapp=2023b.ido.ronen&userEmail=jill@demo.org
		
		// THEN the server responds with 403 - FORBIDDEN
		
		assertThatThrownBy(()->
		this.restTemplate.getForObject(this.baseUrl + "/admin/miniapp?userSuperapp={superapp}&userEmail={email}",
				MiniAppCommandBoundary[].class,
				this.springApplicationName, testUserEmail))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.FORBIDDEN.value());
		
	}


	@Test
	@DisplayName("Test export all commands with empty commands collection succeeds") 
	public void testExportAllCommandsWithEmptyCommandsCollectionSucceeds() {
		// GIVEN the server is up
		// AND the MINIAPPCOMMANDS collection is empty
		
		// WHEN  I GET /superapp/admin/miniapp
		
		MiniAppCommandBoundary[] allCommandsInDatabase = this.restTemplate
				.getForObject(this.baseUrl + "/admin/miniapp?userSuperapp={superapp}&userEmail={email}", MiniAppCommandBoundary[].class,
						this.springApplicationName, this.testAdminUserEmail);
		
		// THEN the server responds with 200 - OK
		// AND the MINIAPPCOMMANDS collection is empty.
		
		assertThat(allCommandsInDatabase)
			.isNotNull()
			.isEmpty();
	}
	
	
	@Test
	@DisplayName("Test export all commands with existing command in the commands collection succeeds")
	public void testExportAllCommandsWithExistingCommandInTheCommandsCollectionSucceeds() {
		// GIVEN the server is up
		// AND the MINIAPPCOMMANDS collection contains at least one miniapp command.
		
		this.testCommand = this.restTemplate.postForObject(this.baseUrl + "/miniapp/{miniAppName}", 
							this.testCommand, 
							MiniAppCommandBoundary.class,
							this.miniAppName);
				
		// WHEN  I GET /superapp/admin/miniapp
		
		MiniAppCommandBoundary[] allCommandsInDatabase = this.restTemplate
				.getForObject(this.baseUrl + "/admin/miniapp?userSuperapp={superapp}&userEmail={email}", MiniAppCommandBoundary[].class,
						this.springApplicationName, this.testAdminUserEmail);
		
		// THEN the server responds with 200 - OK
		// AND the MINIAPPCOMMANDS collection contains only that command
		
		assertThat(allCommandsInDatabase)
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(this.testCommand);
	}
}

