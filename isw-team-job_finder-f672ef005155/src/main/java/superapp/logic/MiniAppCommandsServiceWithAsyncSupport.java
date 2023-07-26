package superapp.logic;

import java.util.List;

import superapp.boundaries.MiniAppCommandBoundary;

public interface MiniAppCommandsServiceWithAsyncSupport extends MiniAppCommandsService {
	
	public Object miniAppCommandAsyncHandler(MiniAppCommandBoundary command);
	public void deleteAllCommands(String userSuperapp, String userEmail);
	public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page);
	public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperapp, String userEmail, int size, int page);
	
}
