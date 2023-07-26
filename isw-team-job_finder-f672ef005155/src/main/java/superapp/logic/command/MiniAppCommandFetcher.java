package superapp.logic.command;

import superapp.data.MiniAppCommandEntity;
import superapp.data.SuperAppObjectEntity;

public interface MiniAppCommandFetcher {
	
	public SuperAppObjectEntity executeCommand(MiniAppCommandEntity command);
	
}
