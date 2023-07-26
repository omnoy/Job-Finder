package superapp.rest_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import superapp.boundaries.MiniAppCommandBoundary;
import superapp.boundaries.sub_boundaries.MiniAppCommandIdBoundary;
import superapp.logic.MiniAppCommandsServiceWithAsyncSupport;

/**
 * The class {@code MiniAppCommandController} is a class which implements the required APIs which
 * are command related, and are not permitted only to administrators.
 * 
 * @author  Rom Gat
 * 			Noy Hanan
 */
@RestController
@Validated
public class MiniAppCommandController {
	private MiniAppCommandsServiceWithAsyncSupport dataManager;
	
	@Autowired
	public MiniAppCommandController(MiniAppCommandsServiceWithAsyncSupport dataManager) {
		super();
		this.dataManager = dataManager;
	}
	
	/**
	 * This method implements an feature <b>Create Command</b> - It takes a MiniAppCommandBoundary
	 * without an id, adds an id to it and returns <b>the affected object</b> by retrieving it from the
	 * layers below. It also passes the MiniAppCommandBoundary in the lower layers for storage.
	 * 
	 * @param	newCommand
	 * 			The MiniAppCommandBoundary representing the new command.
	 * @param	miniAppName
	 * 			The miniapp's name for identification.
	 */
	@RequestMapping(
		path = {"/superapp/miniapp/{miniAppName}"},
		method = {RequestMethod.POST},
		produces = {MediaType.APPLICATION_JSON_VALUE},
		consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Object createCommand (
			@Valid @RequestBody MiniAppCommandBoundary newCommand,
			@PathVariable("miniAppName") String miniAppName,
			@RequestParam(name = "async", required = false, defaultValue = "false") boolean async) {
		Object rv;

		MiniAppCommandIdBoundary commandId = new MiniAppCommandIdBoundary();
		commandId.setMiniapp(miniAppName);
		newCommand.setCommandId(commandId);
		
		if (!async)
			rv = dataManager.invokeCommand(newCommand);
		else 
			rv = dataManager.miniAppCommandAsyncHandler(newCommand);

		return rv;
	}
}
