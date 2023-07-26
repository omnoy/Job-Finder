package superapp.logic.converters;

import superapp.boundaries.sub_boundaries.MiniAppCommandIdBoundary;
import superapp.data.MiniAppCommandIdEntity;

public class MiniAppCommandIdConverter {
	public MiniAppCommandIdEntity commandIdBoundaryToMiniAppCommandIdEntity(MiniAppCommandIdBoundary idBoundary) {
		//Create new entity
		MiniAppCommandIdEntity idEntity = new MiniAppCommandIdEntity();
		
		//Set fields
		idEntity.setSuperapp(idBoundary.getSuperapp());
		idEntity.setMiniapp(idBoundary.getMiniapp());
		idEntity.setInternalCommandId(idBoundary.getInternalCommandId());
		
		return idEntity;
	}
	
	public MiniAppCommandIdBoundary miniAppCommandIdEntityToCommandIdBoundary(MiniAppCommandIdEntity idEntity) {
		//Create new boundary
		MiniAppCommandIdBoundary idBoundary = new MiniAppCommandIdBoundary();
		
		//Set fields
		idBoundary.setSuperapp(idEntity.getSuperapp());
		idBoundary.setMiniapp(idEntity.getMiniapp());
		idBoundary.setInternalCommandId(idEntity.getInternalCommandId());
		
		return idBoundary;
	}
}
