package superapp.logic.command;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import superapp.data.MiniAppCommandEntity;
import superapp.data.SuperAppObjectEntity;

/**This class implements the feature of "Get Jobs by Tags". There are a few reasons why
 * this command exists:
 * <ul>
 * 	<li>There is no existing feature of getting by tags.</li>
 * 	<li>This command is meant to pull from the internet, not from the database, meaning that it does not
 * 		qualify as a simple get operation. As of now, our implementation relies on objects inside the database.</li>
 * </ul>
 * 
 * Later on, this command should have an access to a scraper, to scrape jobs from job listings websites.
 * 
 * @param 	command
 * 			The command to parse for creation.
 */
public class GetJobsByTagsCommand extends Command {
	private List<String> tags;
	
	@SuppressWarnings("unchecked")
	public GetJobsByTagsCommand(MiniAppCommandEntity command) {
		this.tags = (List<String>)command.getCommandAttributes().get("tags");
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SuperAppObjectEntity> execute() {
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
		
		return this.commandResources.getDatabaseObjectCrud().findJobsByTags(this.tags);
	}

}
