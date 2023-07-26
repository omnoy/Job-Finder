package superapp.logic.command;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import superapp.data.MiniAppCommandEntity;
import superapp.data.SuperAppObjectEntity;

@Component
public class CommandBroker {
	Map<String, MiniAppCommandManager> miniAppsMap;
	
	public CommandBroker(JobsGathererManager jobsGatherer,
			ResumeBuilderManager resumeBuilder) {
		this.miniAppsMap = new HashMap<>();
		this.miniAppsMap.put("jobsGatherer", jobsGatherer);
		this.miniAppsMap.put("resumeBuilder", resumeBuilder);
	}
	
	/**
	 * 
	 * @param 	command
	 * 			Command entity to execute.
	 * 
	 * @return Object to return to calling user.
	 */
	public List<SuperAppObjectEntity> executeCommand(MiniAppCommandEntity command) {
		return miniAppsMap.get(command.getCommandId().getMiniapp()).executeCommand(command);
	}
}
