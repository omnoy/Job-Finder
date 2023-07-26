package superapp.logic.mongo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.MiniAppCommandBoundary;
import superapp.boundaries.sub_boundaries.MiniAppCommandIdBoundary;
import superapp.data.MiniAppCommandEntity;
import superapp.data.SuperAppObjectEntity;
import superapp.data.SuperAppObjectIdEntity;
import superapp.data.UserEntity;
import superapp.data.UserIdEntity;
import superapp.data.UserRole;
import superapp.data.crud.MiniAppCommandCrud;
import superapp.data.crud.SuperAppObjectCrud;
import superapp.logic.HTTPBadRequestException;
import superapp.logic.HTTPForbiddenException;
import superapp.logic.MiniAppCommandsServiceWithAsyncSupport;
import superapp.logic.command.CommandBroker;
import superapp.logic.components.SuperAppObjectAuthenticationComponent;
import superapp.logic.components.UserAuthenticationComponent;
import superapp.logic.converters.MiniAppCommandConverter;
import superapp.logic.converters.SuperAppObjectConverter;

/**
 * This class implements the MiniAppCommandsService. It connects to MongoDB
 * using a CRUD instance for commands.
 * 
 * @author Rom Gat Dori Rozen Daniel Shitrit
 */

@Service
public class MiniAppCommandDataManagerMongoDB implements MiniAppCommandsServiceWithAsyncSupport {
	private String springApplicationName;
	private MiniAppCommandCrud databaseCrud;
	private MiniAppCommandConverter miniAppCommandConverter;
	private SuperAppObjectCrud superappDatabaseCrud;

	private ObjectMapper jackson;
	private JmsTemplate jmsTemplate;
	private String jmsDestination;

	private UserAuthenticationComponent userAuthentication;
	private SuperAppObjectAuthenticationComponent superAppObjectAuthentication;

	private CommandBroker commandBroker;
	private SuperAppObjectConverter superAppObjectConverter;

	public MiniAppCommandDataManagerMongoDB(MiniAppCommandCrud databaseCrud,
			SuperAppObjectCrud superappDatabaseCrud,
			UserAuthenticationComponent userAuthentication,
			SuperAppObjectAuthenticationComponent superAppObjectAuthentication, CommandBroker commandBroker) {
		this.databaseCrud = databaseCrud;
		this.userAuthentication = userAuthentication;
		this.superAppObjectAuthentication = superAppObjectAuthentication;
		this.commandBroker = commandBroker;
		this.superappDatabaseCrud = superappDatabaseCrud;
	}

	@Autowired
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
		this.jmsTemplate.setDeliveryDelay(3000L);
	}

	@Value("${spring.jms.template.default-destination}")
	public void setJmsDestination(String jmsDestination) {
		this.jmsDestination = jmsDestination;
	}

	@Value("${spring.application.name}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	@PostConstruct
	public void init() {
		this.miniAppCommandConverter = new MiniAppCommandConverter();
		this.superAppObjectConverter = new SuperAppObjectConverter();
		System.err.println("***** " + this.springApplicationName);
		this.jackson = new ObjectMapper();
	}

	private MiniAppCommandBoundary validateCommand(MiniAppCommandBoundary command) {
		if (userAuthentication.getUserRoleNameById(command.getInvokedBy().getUserId().getSuperapp(),
				command.getInvokedBy().getUserId().getEmail()) != UserRole.MINIAPP_USER.toString())
			throw new HTTPForbiddenException("ADMINs cannot invoke Miniapp Commands.");

		if (command.getCommandId() == null || command.getCommandId().getMiniapp() == null) {
			throw new HTTPBadRequestException("Invalid command - Miniapp name not specified");
		}

		if (superAppObjectAuthentication.getSuperAppObjectBoundary(command.getTargetObject().getObjectId()) == null)
			throw new HTTPBadRequestException("SuperAppObject is not registered");

		if (superAppObjectAuthentication.getSuperAppObjectBoundary(command.getTargetObject().getObjectId())
				.getActive() == false)
			throw new HTTPBadRequestException("SuperAppObject is not active");

		if (command.getCommandAttributes() == null) {
			command.setCommandAttributes(new HashMap<String, Object>());
		}

		MiniAppCommandIdBoundary commandId = new MiniAppCommandIdBoundary();
		commandId.setSuperapp(this.springApplicationName);
		commandId.setMiniapp(command.getCommandId().getMiniapp());
		commandId.setInternalCommandId(UUID.randomUUID().toString());

		command.setCommandId(commandId);

		command.setInvocationTimestamp(new Date());

		return command;
	}

	@Override
	public Object invokeCommand(MiniAppCommandBoundary command) {
		// validate user permissions
		command = validateCommand(command);

		MiniAppCommandEntity commandEntity = this.miniAppCommandConverter
				.commandBoundaryToMiniAppCommandEntity(command);

		Object rv = null;

		if (commandEntity.getCommandId().getMiniapp() != "testMiniApp") {
			// execute command
			rv = this.commandBroker.executeCommand(commandEntity).stream()
					.map(this.superAppObjectConverter::superAppObjectEntityToSuperAppObjectBoundary).toList();
		} else {
			rv = command;
		}

		// then save to database
		this.databaseCrud.save(commandEntity);

		return rv;
	}

	@Override
	@Deprecated
	public List<MiniAppCommandBoundary> getAllCommands() {
		return this.databaseCrud.findAll().stream()
				.map(this.miniAppCommandConverter::miniAppCommandEntityToMiniAppCommandBoundary).toList();
	}

	@Override
	@Deprecated
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
		return this.databaseCrud.findAll().stream()
				.filter(commandEntity -> commandEntity.getCommandId().getMiniapp().equals(miniAppName))
				.map(this.miniAppCommandConverter::miniAppCommandEntityToMiniAppCommandBoundary).toList();
	}

	@Override
	@Deprecated
	public void deleteAllCommands() {
		this.databaseCrud.deleteAll();
	}

	@Override
	public Object miniAppCommandAsyncHandler(MiniAppCommandBoundary command) {
		command = validateCommand(command);
		
		try {
//			MiniAppCommandEntity commandEntity = this.miniAppCommandConverter
//					.commandBoundaryToMiniAppCommandEntity(command);
			
			// preprocess the target object 
			this.objectPreprocessAsync(command);
			
			String commandJson = this.jackson.writeValueAsString(command);

			// send Json to Artemis queue
			this.jmsTemplate.convertAndSend(this.jmsDestination, commandJson);
			System.err.println("*** sending command: " + command.getCommand());

			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@JmsListener(destination = "${spring.jms.template.default-destination}")
	public void handleCommand(String commandJson) {
		try {
			MiniAppCommandBoundary command = this.jackson.readValue(commandJson, MiniAppCommandBoundary.class);
			MiniAppCommandEntity commandEntity = this.miniAppCommandConverter
					.commandBoundaryToMiniAppCommandEntity(command);

			List<SuperAppObjectEntity> rv = this.commandBroker.executeCommand(commandEntity);

			this.databaseCrud.save(commandEntity);

			System.err.println("*** Finished executing command: " + command.getCommand());

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private void objectPreprocessAsync(MiniAppCommandBoundary command) {
		MiniAppCommandEntity commandEntity = this.miniAppCommandConverter
				.commandBoundaryToMiniAppCommandEntity(command);
		
		UserIdEntity invokerId = commandEntity.getInvokedBy();
		
		SuperAppObjectIdEntity appId = commandEntity.getTargetObject();
		SuperAppObjectEntity app = this.superappDatabaseCrud.findById(appId).orElseThrow();
		
		app.getObjectDetails().put("status", "inProcess");
		app.getObjectDetails().put("requestingUser", invokerId);
		
		this.superappDatabaseCrud.save(app);
	}

	/////////////////////////////////////////////
	// UPDATED ADMIN API
	/////////////////////////////////////////////

	@Override
	public void deleteAllCommands(String userSuperapp, String userEmail) {
		// check permissions
		if (userAuthentication.getUserRoleNameById(userSuperapp, userEmail) != UserRole.ADMIN.toString())
			throw new HTTPForbiddenException("Only ADMIN can delete all commands.");

		this.databaseCrud.deleteAll();
	}

	@Override
	public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page) {
		// check permissions
		if (userAuthentication.getUserRoleNameById(userSuperapp, userEmail) != UserRole.ADMIN.toString())
			throw new HTTPForbiddenException("Only ADMIN can get all commands.");

		return this.databaseCrud
				.findAll(
						PageRequest.of(page, size, Direction.ASC, "invocationTimestamp", "commandId.internalCommandId"))
				.stream().map(this.miniAppCommandConverter::miniAppCommandEntityToMiniAppCommandBoundary).toList();
	}

	@Override
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail,
			int size, int page) {
		// check permissions
		if (userAuthentication.getUserRoleNameById(userSuperapp, userEmail) != UserRole.ADMIN.toString())
			throw new HTTPForbiddenException("Only ADMIN can get all miniapp commands.");

		return this.databaseCrud
				.findAll(
						PageRequest.of(page, size, Direction.ASC, "invocationTimestamp", "commandId.internalCommandId"))
				.stream().filter(commandEntity -> commandEntity.getCommandId().getMiniapp().equals(miniAppName))
				.map(this.miniAppCommandConverter::miniAppCommandEntityToMiniAppCommandBoundary).toList();
	}
}
