package superapp.logic.command;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import superapp.data.MiniAppCommandEntity;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserIdEntity;

public class GetFavoriteJobsByTagsCommand extends Command {
	private List<String> tags;
	private UserIdEntity userId;
	
	@SuppressWarnings("unchecked")
	public GetFavoriteJobsByTagsCommand(MiniAppCommandEntity command) {
		this.tags = (List<String>)command.getCommandAttributes().get("tags");
		this.userId = command.getInvokedBy();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SuperAppObjectEntity> execute() {
		List<String> favoriteJobInternalIds = (List<String>)this.commandResources.getDatabaseObjectCrud()
				.getUserRecord(this.userId.getSuperapp(), this.userId.getEmail())
				.get(0)
				.getObjectDetails().get("favoriteJobInternalIds");
		
		if (this.tags.isEmpty()) {
			this.tags = this.commandResources.getDatabaseObjectCrud()
					.findAllByAlias("jobSelectorOptions",
							PageRequest.of(0, 1, Direction.DESC, "creationTimeStamp", "objectId.interalObjectId"))
					.get(0)
					.getObjectDetails()
					.values()
					.stream()
					.flatMap(l -> ((List<String>)l).stream())
					.collect(Collectors.toList());
			System.err.println(this.tags);
		}
		
		return this.commandResources.getDatabaseObjectCrud().findJobsByInternalIdsAndTags(favoriteJobInternalIds, this.tags);
	}

}
