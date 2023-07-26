package superapp.logic;

import java.util.List;

import superapp.boundaries.SuperAppObjectBoundary;

/**
 * 
 * @author 
 * Omer Noy 
 * Ido Ronen
 */


public interface ObjectsService{

    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary object);
    @Deprecated
    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update);
    @Deprecated
    public SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId);
    @Deprecated
    public List<SuperAppObjectBoundary> getAllObjects();
    @Deprecated
    public void deleteAllObjects();
}