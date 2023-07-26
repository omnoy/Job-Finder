package superapp.boundaries;

import java.util.Date;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import superapp.boundaries.sub_boundaries.*;

/**
 * The class {@code MiniAppCommandBoundary} is a class which represents a boundary for
 * any command invoked through a miniapp by a user in the project. The class follows the
 * Java Beans rules so it could be marshaled and unmarshaled.
 * 
 * <p>
 * This boundary is composed of:
 * <ul>
 * 	<li>A CommandIdBoundary representing the command's identification</li>
 * 	<li>A String describing the command</li>
 * 	<li>An ObjectTargeterBoundary representing the targeted object</li>
 * 	<li>A Date representing the command's invocation time</li>
 * 	<li>A CommandInvokerBoundary representing the invoking user</li>
 * 	<li>A Map of (String, Object) pairs representing the command's contents</li>
 * </ul>
 * </p>
 * 
 * @author 	Omer Noy
 */
public class MiniAppCommandBoundary {
	private MiniAppCommandIdBoundary commandId;
	
	@NotBlank(message = "Invalid command - Command definition not specified")
	private String command;
	
	@Valid
	@NotNull(message = "Invalid command - Target object not specified")
	private SuperAppObjectTargeterBoundary targetObject;
	
	private Date invocationTimestamp;
	
	@Valid
	@NotNull(message = "Invalid command - Command invoker not specified")
	private CommandInvokerBoundary invokedBy;
	private Map<String, Object> commandAttributes;
	
	
	public MiniAppCommandBoundary() {
		//do nothing
	}
	
	public MiniAppCommandIdBoundary getCommandId() {
		return commandId;
	}
	
	public void setCommandId(MiniAppCommandIdBoundary commandId) {
		this.commandId = commandId;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public SuperAppObjectTargeterBoundary getTargetObject() {
		return targetObject;
	}
	
	public void setTargetObject(SuperAppObjectTargeterBoundary targetObject) {
		this.targetObject = targetObject;
	}
	
	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}
	
	public void setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
	}
	
	public CommandInvokerBoundary getInvokedBy() {
		return invokedBy;
	}
	
	public void setInvokedBy(CommandInvokerBoundary invokedBy) {
		this.invokedBy = invokedBy;
	}
	
	public Map<String, Object> getCommandAttributes() {
		return commandAttributes;
	}
	
	public void setCommandAttributes(Map<String, Object> commandAttributes) {
		this.commandAttributes = commandAttributes;
	}

	@Override
	public String toString() {
		return "MiniAppCommandBoundary [commandId=" + commandId + ", command=" + command + ", targetObject="
				+ targetObject + ", invocationTimestamp=" + invocationTimestamp + ", invokedBy=" + invokedBy
				+ ", commandAttributes=" + commandAttributes + "]";
	}
}
