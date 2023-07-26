package superapp.logic.command;

import org.springframework.stereotype.Component;

import superapp.data.MiniAppCommandEntity;

@Component
public class ResumeBuilderManager extends MiniAppCommandManager {
	
	public ResumeBuilderManager(CommandsResourcesComponent commandResources) {
		super(commandResources);
	}
	
	@Override
	public Command getCommand(MiniAppCommandEntity command) {
		switch (command.getCommand()) {
		case "BUILD_RESUME":
			return new BuildResumeCommand(command);
		case "GET_RESUMES":
			return new GetResumesCommand(command);
		default:
			return super.getCommand(command);
		}
	}

}
