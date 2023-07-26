package superapp.miniapp_command_api_tests;

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
import superapp.boundaries.MiniAppCommandBoundary;
import superapp.boundaries.NewUserBoundary;
import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.UserBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectTargeterBoundary;
import superapp.boundaries.sub_boundaries.UserIdBoundary;
import superapp.boundaries.sub_boundaries.CommandInvokerBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectCreatorBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;


/**
 * 
 * @author noyhanan
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class MiniappCommandCreationTests {
	private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	private String springApplicationName;
	private String miniAppName;
	private MiniAppCommandBoundary testObject;
	
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
		this.miniAppName = "jobFinder";
		
		this.superappUserEmail = "superAppUser@gmail.com";
		this.miniappUserEmail = "miniAppUser@gmail.com";
		this.adminEmail = "admin@gmail.com";
	}
	
	@BeforeEach
	public void resetState() {
		// Tear down DB:
		tearDown();
		
		// Also, create/reset some basic instances for code-simplicity:
		
		// Add new superapp user to the database
		NewUserBoundary user = new NewUserBoundary();
		
		user.setEmail(this.superappUserEmail);
		user.setUsername("superAppUser");
		user.setAvatar("S");
		user.setRole("SUPERAPP_USER");
		
		this.restTemplate
			.postForObject(baseUrl + "/users", user, UserBoundary.class);
		
		// Add new miniapp user to the database
		user.setEmail(this.miniappUserEmail);
		user.setUsername("miniAppUser");
		user.setAvatar("M");
		user.setRole("MINIAPP_USER");
		
		this.restTemplate
			.postForObject(baseUrl + "/users", user, UserBoundary.class);
		
		// Add new admin user to the database
		user.setEmail(this.adminEmail);
		user.setUsername("admin");
		user.setAvatar("A");
		user.setRole("ADMIN");
		
		this.restTemplate
			.postForObject(baseUrl + "/users", user, UserBoundary.class);
		
		// Add new active target object to the dataset 
		SuperAppObjectBoundary suparAppObject = new SuperAppObjectBoundary();
		suparAppObject.setAlias("test alias");
		suparAppObject.setType("TargetObject");
		suparAppObject.setActive(true);
		UserIdBoundary createdBy = new UserIdBoundary();
		createdBy.setSuperapp(this.springApplicationName);
		createdBy.setEmail(this.superappUserEmail);
		SuperAppObjectCreatorBoundary objectCreator = new SuperAppObjectCreatorBoundary();
		objectCreator.setUserId(createdBy);
		suparAppObject.setCreatedBy(objectCreator);
		
		SuperAppObjectBoundary testSuparAppObject = this.restTemplate
			.postForObject(this.baseUrl + "/objects", suparAppObject, SuperAppObjectBoundary.class);

		
		
		// A valid object boundary
		MiniAppCommandBoundary miniAppCommandBoundary = new MiniAppCommandBoundary();
		
		// set miniAppCommandBoundary attributes
		// command
		miniAppCommandBoundary.setCommand("I'm a test command");
		
		
		// targetObject
		SuperAppObjectTargeterBoundary targetBoundary = new SuperAppObjectTargeterBoundary();
		SuperAppObjectIdBoundary targetIdBoundary = new SuperAppObjectIdBoundary();
		targetIdBoundary.setSuperapp(testSuparAppObject.getObjectId().getSuperapp());
		targetIdBoundary.setInternalObjectId(testSuparAppObject.getObjectId().getInternalObjectId());
		targetBoundary.setObjectId(targetIdBoundary);
		miniAppCommandBoundary.setTargetObject(targetBoundary);

		
		
		// invokedBy
		CommandInvokerBoundary invokerByBoundary = new CommandInvokerBoundary();
		UserIdBoundary invokerByIdBoundary = new UserIdBoundary();
		invokerByIdBoundary.setSuperapp(this.springApplicationName);
		invokerByIdBoundary.setEmail(this.miniappUserEmail);
		invokerByBoundary.setUserId(invokerByIdBoundary);
		miniAppCommandBoundary.setInvokedBy(invokerByBoundary);
		
		this.testObject = miniAppCommandBoundary;
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
	@DisplayName("Test object creation with valid input succeeds")
	public void testObjectCreationWithValidInputSucceeds() {
		// GIVEN the server is up
		// AND the MINIAPPCOMMANDS collection is empty
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
				"superapp": "2023b.demo",
				"internalObjectId": "1"
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				    }
            }
		}
		*/
		MiniAppCommandBoundary returnedObject = this.restTemplate
				.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
						this.testObject, 
						MiniAppCommandBoundary.class);
		
		// THEN the MINIAPPCOMMANDS contains a single object with (But not limited to)
		/*	
		{
		    "commandId": {
		        "superapp": "2023b.ido.ronen",
		        "miniapp": "test",
		        "internalCommandId": "31dcbd3d-1b4c-4c71-ae37-0ab8ba104335"
		    },
		    "command": "doSomething",
		    "targetObject": {
		        "objectId": {
		            "superapp": "2023b.demo",
		            "internalObjectId": "1"
		        }
		    },
		    "invokedBy": {
		        "userId": {
		            "superapp": "2023b.demo",
		            "email": "jane@demo.org"
		        }
		    },
		    "commandAttributes": {}
		}
		 */
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/admin/miniapp?userSuperapp=" +
								 this.springApplicationName + "&userEmail=" + 
								 this.adminEmail , MiniAppCommandBoundary[].class))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(returnedObject);
	}
	
	@Test
	@DisplayName("Test object creation with null command fails")
	public void testObjectCreationWithNullCommandFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : null,
			"targetObject": {
				"objectId":	 {
				"superapp": "2023b.demo",
				"internalObjectId": "1"
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				}
            }
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.setCommand(null);
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("Test object creation with blank command fails")
	public void testObjectCreationWithBlankCommandFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": "1"
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				}
            }
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.setCommand("");
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation without targetObject fails")
	public void testObjectCreationWithNullTargetObjectFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": null
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				}
            }
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.setTargetObject(null);
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	
	@Test
	@DisplayName("Test object creation with null targetObject.objectId fails")
	public void testObjectCreationWithNullTargetObjectObjectIdFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	null
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				}
            }
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getTargetObject().setObjectId(null);
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with null targetObject.objectId.superapp fails")
	public void testObjectCreationWithNullTargetObjectSuperappFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": null,
					"internalObjectId": "1"
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				}
            }
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getTargetObject().getObjectId().setSuperapp(null);
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with blank targetObject.objectId.superapp fails")
	public void testObjectCreationWithBlankTargetObjectSuperappFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "",
					"internalObjectId": "1"
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				}
            }
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getTargetObject().getObjectId().setSuperapp("");
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	
	@Test
	@DisplayName("Test object creation with null targetObject.objectId.internalObjectId fails")
	public void testObjectCreationWithNullTargetObjectInternalObjectIdFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": null
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				}
            }
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getTargetObject().getObjectId().setInternalObjectId(null);
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with blank targetObject.objectId.internalObjectId fails")
	public void testObjectCreationWithBlankTargetObjectInternalObjectIdFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": ""
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				}
            }
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getTargetObject().getObjectId().setInternalObjectId("");
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	
	@Test
	@DisplayName("Test object creation without invokedBy fails")
	public void testObjectCreationWithNullInvokedByFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": ""
				}
			},
			"invokedBy": null
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.setInvokedBy(null);
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with null invokedBy.userId fails")
	public void testObjectCreationWithNullInvokedByUserIdFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": ""
				}
			},
			"invokedBy": {
				"userId": null
        	}
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getInvokedBy().setUserId(null);
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with null invokedBy.userId.superapp fails")
	public void testObjectCreationWithNullInvokedByUserIdSuperappFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": ""
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": null,
					"email": "jane@demo.org"
				}
        	}
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getInvokedBy().getUserId().setSuperapp(null);
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with blank invokedBy.userId.superapp fails")
	public void testObjectCreationWithBlankInvokedByUserIdSuperappFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": ""
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "",
					"email": "jane@demo.org"
				}
        	}
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getInvokedBy().getUserId().setSuperapp("");
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with null invokedBy.userId.email fails")
	public void testObjectCreationWithNullInvokedByUserIdEmailFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": ""
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": null
				}
        	}
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getInvokedBy().getUserId().setEmail(null);
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with blank invokedBy.userId.email fails")
	public void testObjectCreationWithBlankInvokedByUserIdEmailFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": ""
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": ""
				}
        	}
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getInvokedBy().getUserId().setEmail("");
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with invokedBy.userId.email not in email format fails")
	public void testObjectCreationWithInvokedByUserIdEmailNotInFormatFails() {
		// GIVEN the server is up
		
		// WHEN I POST /superapp/miniapp/{miniAppName} with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": ""
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "test"
				}
        	}
		}
		*/
		// THEN the server responds with 400 - Bad Request
		this.testObject.getInvokedBy().getUserId().setEmail("test");
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					this.testObject, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with user role different from miniapp user fails")
	public void testObjectCreationWithUserRoleDifferentFromMiniappUserFails() {
	// GIVEN the server is up
	
			// WHEN I POST /superapp/miniapp/{miniAppName} with this JSON that includes
			//user email that is not of a MINIAPP_USER
			/*	
			{
				"command" : "doSomething",
				"targetObject": {
					"objectId":	 {
						"superapp": "2023b.demo",
						"internalObjectId": ""
					}
				},
				"invokedBy": {
					"userId": {
						"superapp": "2023b.demo",
						"email": "admin@gmail.com"
					}
	        	}
			}
			*/
			// THEN the server responds with 403 - Forbidden
			this.testObject.getInvokedBy().getUserId().setEmail(this.adminEmail);
					
			assertThatThrownBy(()->
			this.restTemplate
				.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
						this.testObject, MiniAppCommandBoundary.class))
				.isInstanceOf(HttpClientErrorException.class)
				.extracting("statusCode.value")
				.isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Test object creation with user non active super app object fails")
	public void testObjectCreationWithUserNonActiveSuperAppObjectFails() {
	// GIVEN the server is up
	
		// WHEN I POST /superapp/miniapp/{miniAppName} with this JSON that includes
		//internalObjectId of a superapp object that is not actice
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
					"superapp": "2023b.demo",
					"internalObjectId": ""
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "admin@gmail.com"
				}
        	}
		}
		*/
	
	
		// create new non active superapp object and add it to the dataset 
		SuperAppObjectBoundary suparAppObject = new SuperAppObjectBoundary();
		suparAppObject.setAlias("test alias");
		suparAppObject.setType("TargetObject");
		suparAppObject.setActive(false);
		UserIdBoundary createdBy = new UserIdBoundary();
		createdBy.setSuperapp(this.springApplicationName);
		createdBy.setEmail(this.superappUserEmail);
		SuperAppObjectCreatorBoundary objectCreator = new SuperAppObjectCreatorBoundary();
		objectCreator.setUserId(createdBy);
		suparAppObject.setCreatedBy(objectCreator);
		
		SuperAppObjectBoundary nonActiveSuparAppObject = this.restTemplate
			.postForObject(this.baseUrl + "/objects", suparAppObject, SuperAppObjectBoundary.class);
		
		
		// create new miniapp command that has the non active superapp object as target			
		MiniAppCommandBoundary currentMiniAppCommandBoundary = new MiniAppCommandBoundary();
		currentMiniAppCommandBoundary.setCommand("I'm a test command");
		
		// targetObject
		SuperAppObjectTargeterBoundary targetBoundary = new SuperAppObjectTargeterBoundary();
		SuperAppObjectIdBoundary targetIdBoundary = new SuperAppObjectIdBoundary();
		targetIdBoundary.setSuperapp(nonActiveSuparAppObject.getObjectId().getSuperapp());
		targetIdBoundary.setInternalObjectId(nonActiveSuparAppObject.getObjectId().getInternalObjectId());
		targetBoundary.setObjectId(targetIdBoundary);
		currentMiniAppCommandBoundary.setTargetObject(targetBoundary);
		
		// invokedBy
		CommandInvokerBoundary invokerByBoundary = new CommandInvokerBoundary();
		UserIdBoundary invokerByIdBoundary = new UserIdBoundary();
		invokerByIdBoundary.setSuperapp(this.springApplicationName);
		invokerByIdBoundary.setEmail(this.miniappUserEmail);
		invokerByBoundary.setUserId(invokerByIdBoundary);
		currentMiniAppCommandBoundary.setInvokedBy(invokerByBoundary);
		
		
		// THEN the server responds with 400 - Bad Request
		
		assertThatThrownBy(()->
		this.restTemplate
			.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName, 
					currentMiniAppCommandBoundary, MiniAppCommandBoundary.class))
			.isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode.value")
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Test object creation with true async flag succeeds")
	public void testObjectCreationWithTrueAsyncFlagSucceeds() {
		// GIVEN the server is up
		// AND the MINIAPPCOMMANDS collection is empty
		
		// WHEN I POST /superapp/miniapp/{miniAppName}?async=true with
		/*	
		{
			"command" : "doSomething",
			"targetObject": {
				"objectId":	 {
				"superapp": "2023b.demo",
				"internalObjectId": "1"
				}
			},
			"invokedBy": {
				"userId": {
					"superapp": "2023b.demo",
					"email": "jane@demo.org"
				    }
            }
		}
		*/	
		MiniAppCommandBoundary returnedObject = this.restTemplate
				.postForObject(this.baseUrl + "/miniapp/" + this.miniAppName + "?async=true", 
						this.testObject, 
						MiniAppCommandBoundary.class);
		
		// AND activate 3 seconds delay
		try {
		    Thread.sleep(3000); // Sleep for 3 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
				
		// THEN the MINIAPPCOMMANDS contains a single object with (But not limited to)
		/*	
		{
		    "commandId": {
		        "superapp": "2023b.ido.ronen",
		        "miniapp": "test",
		        "internalCommandId": "31dcbd3d-1b4c-4c71-ae37-0ab8ba104335"
		    },
		    "command": "doSomething",
		    "targetObject": {
		        "objectId": {
		            "superapp": "2023b.demo",
		            "internalObjectId": "1"
		        }
		    },
		    "invokedBy": {
		        "userId": {
		            "superapp": "2023b.demo",
		            "email": "jane@demo.org"
		        }
		    },
		    "commandAttributes": {}
		}
		 */
		assertThat(this.restTemplate
				.getForObject(this.baseUrl + "/admin/miniapp?userSuperapp=" +
								 this.springApplicationName + "&userEmail=" + 
								 this.adminEmail , MiniAppCommandBoundary[].class))
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactly(returnedObject);
			
	}
	
	
}
