package superapp.data;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "MINIAPPCOMMANDS")
public class MiniAppCommandEntity {
	@Id private MiniAppCommandIdEntity commandId;
	private String command;
	private SuperAppObjectIdEntity targetObject;
	private Date invocationTimestamp;
	private UserIdEntity invokedBy;
	private Map<String, Object> commandAttributes;
	
	public MiniAppCommandEntity() {
	}

	public MiniAppCommandIdEntity getCommandId() {
		return commandId;
	}

	public void setCommandId(MiniAppCommandIdEntity commandId) {
		this.commandId = commandId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public SuperAppObjectIdEntity getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(SuperAppObjectIdEntity targetObject) {
		this.targetObject = targetObject;
	}

	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}

	public void setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
	}

	public UserIdEntity getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(UserIdEntity invokedBy) {
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
		return "MiniAppCommandEntity [commandId=" + commandId + ", command=" + command + ", targetObject="
				+ targetObject + ", invocationTimestamp=" + invocationTimestamp + ", invokedBy=" + invokedBy
				+ ", commandAttributes=" + commandAttributes + "]";
	}
}
