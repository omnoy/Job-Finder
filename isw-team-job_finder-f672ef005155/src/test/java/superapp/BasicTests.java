package superapp;

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
import superapp.boundaries.UserBoundary;

/** This test-case contains the most basic and general of tests, such as contextLoad.
 * 
 * @author Rom Gat
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class BasicTests {
	private RestTemplate restTemplate;
	private String baseUrl;
	private int port;
	
	private String springApplicationName;
	
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
	@DisplayName("Test server loading succeeds")
	public void contextLoads() {
	}
}
