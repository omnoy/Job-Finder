package superapp.logic.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import superapp.data.crud.MiniAppCommandCrud;
import superapp.data.crud.SuperAppObjectCrud;
import superapp.data.crud.UserCrud;

@Component
public class CommandsResourcesComponent {
	private UserCrud databaseUserCrud;
	private SuperAppObjectCrud databaseObjectCrud;
	private MiniAppCommandCrud databaseCommandCrud;
	
	private String springApplicationName;
	private String superuserEmail;
	private String adminEmail;
	
	public CommandsResourcesComponent(UserCrud databaseUserCrud,
			SuperAppObjectCrud databaseObjectCrud,
			MiniAppCommandCrud databaseCommandCrud) {
		this.databaseUserCrud = databaseUserCrud;
		this.databaseObjectCrud = databaseObjectCrud;
		this.databaseCommandCrud = databaseCommandCrud;
	}
	
	@Value("${spring.application.name}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}
	
	@Value("${config.superuser.email}")
	public void setSuperuserEmail(String superuserEmail) {
		this.superuserEmail = superuserEmail;
	}
	
	@Value("${config.admin.email}")
	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public UserCrud getDatabaseUserCrud() {
		return databaseUserCrud;
	}

	public SuperAppObjectCrud getDatabaseObjectCrud() {
		return databaseObjectCrud;
	}

	public MiniAppCommandCrud getDatabaseCommandCrud() {
		return databaseCommandCrud;
	}

	public String getSpringApplicationName() {
		return springApplicationName;
	}

	public String getSuperuserEmail() {
		return superuserEmail;
	}

	public String getAdminEmail() {
		return adminEmail;
	}
}
