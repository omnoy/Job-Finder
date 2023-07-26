package superapp.logic;

import java.util.List;

import superapp.boundaries.MiniAppCommandBoundary;

/**
 * 
 * @author 
 * Rom Gat
 */

public interface MiniAppCommandsService {
	public Object invokeCommand(MiniAppCommandBoundary command);
	@Deprecated
	public List<MiniAppCommandBoundary> getAllCommands();
	@Deprecated
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName);
	@Deprecated
	public void deleteAllCommands();
}
