package superapp.logic.command;

import java.util.List;

import superapp.data.SuperAppObjectEntity;

public abstract class Command {
	protected CommandsResourcesComponent commandResources;
	
	public void initialize(CommandsResourcesComponent commandResources) {
		this.commandResources = commandResources;
	}
	
	public abstract List<SuperAppObjectEntity> execute();
}
