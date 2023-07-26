package superapp.logic;

import java.util.List;

import superapp.boundaries.UserBoundary;

/**
 * 
 * @author Ido Ronen
 */

public interface UpdatedUserService extends UserService {
	public List<UserBoundary> getAllUsers(String userSuperapp, String userEmail, int size, int page);
	public void deleteAllUsers(String userSuperapp, String userEmail);
}
