package superapp.logic.command;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import superapp.data.MiniAppCommandEntity;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserIdEntity;

public class GetResumesCommand extends Command {
	private static int NUM_RESUMES = 10;
	private static int PAGE = 0;
	private UserIdEntity invokerId;

	public GetResumesCommand(MiniAppCommandEntity command) {
		this.invokerId = command.getInvokedBy();
	}

	@Override
	public List<SuperAppObjectEntity> execute() {
		return this.commandResources.getDatabaseObjectCrud().find10LatestResumes(
				this.invokerId.getSuperapp(), this.invokerId.getEmail(),
				PageRequest.of(PAGE, NUM_RESUMES, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"));
	}

}
