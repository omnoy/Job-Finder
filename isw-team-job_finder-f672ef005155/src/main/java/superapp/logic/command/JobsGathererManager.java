package superapp.logic.command;

import org.springframework.stereotype.Component;

import superapp.data.MiniAppCommandEntity;

@Component
public class JobsGathererManager extends MiniAppCommandManager {
	
	public JobsGathererManager(CommandsResourcesComponent commandResources) {
		super(commandResources);
	}

	@Override
	public Command getCommand(MiniAppCommandEntity command) {
		switch (command.getCommand()) {
		case "getJobsByTags":
			return new GetJobsByTagsCommand(command);
		case "getFavoriteJobsByTags":
			return new GetFavoriteJobsByTagsCommand(command);
		default:
			return super.getCommand(command);
		}
	}

}
