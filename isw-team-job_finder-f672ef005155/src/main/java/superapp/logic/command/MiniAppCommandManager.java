package superapp.logic.command;

import java.util.List;

import superapp.data.MiniAppCommandEntity;
import superapp.data.SuperAppObjectEntity;
import superapp.logic.HTTPBadRequestException;

public abstract class MiniAppCommandManager {
	protected CommandsResourcesComponent commandResources;
	
	public MiniAppCommandManager(CommandsResourcesComponent commandResources) {
		this.commandResources = commandResources;
	}
	
	public Command getCommand(MiniAppCommandEntity command) {
		switch (command.getCommand()) {
		case "getUserRecord":
			return new GetUserRecordsCommand(command);
		default:
			throw new HTTPBadRequestException("Unknown command: " + command.getCommand());
		}
	}
	
	public List<SuperAppObjectEntity> executeCommand(MiniAppCommandEntity commandEntity) {
		Command command = getCommand(commandEntity);
		command.initialize(this.commandResources);
		return command.execute();
	}
}
