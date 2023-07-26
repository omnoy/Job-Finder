package superapp.logic.command;

import java.util.List;

import superapp.data.MiniAppCommandEntity;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserIdEntity;

public class GetUserRecordsCommand extends Command {
	private UserIdEntity userId;
	
	public GetUserRecordsCommand(MiniAppCommandEntity command) {
		this.userId = command.getInvokedBy();
	}

	@Override
	public List<SuperAppObjectEntity> execute() {
		return this.commandResources.getDatabaseObjectCrud()
				.getUserRecord(this.userId.getSuperapp(), this.userId.getEmail());
	}

}
