package superapp.logic.converters;

import superapp.boundaries.MiniAppCommandBoundary;
import superapp.boundaries.sub_boundaries.CommandInvokerBoundary;
import superapp.boundaries.sub_boundaries.MiniAppCommandIdBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectTargeterBoundary;
import superapp.boundaries.sub_boundaries.UserIdBoundary;
import superapp.data.MiniAppCommandEntity;
import superapp.data.MiniAppCommandIdEntity;
import superapp.data.SuperAppObjectIdEntity;
import superapp.data.UserIdEntity;

public class MiniAppCommandConverter {
	private MiniAppCommandIdConverter miniAppCommandIdConverter;
	private SuperAppObjectIdConverter superAppObjectIdConverter;
	private UserIdConverter userIdConvertor;
	
	public MiniAppCommandConverter() {
		this.miniAppCommandIdConverter = new MiniAppCommandIdConverter();
		this.superAppObjectIdConverter = new SuperAppObjectIdConverter();
		this.userIdConvertor = new UserIdConverter();
	}
	
	public MiniAppCommandEntity commandBoundaryToMiniAppCommandEntity(MiniAppCommandBoundary commandBoundary) {
		MiniAppCommandEntity commandEntity = new MiniAppCommandEntity();
		
		MiniAppCommandIdEntity idEntity = this.miniAppCommandIdConverter
				.commandIdBoundaryToMiniAppCommandIdEntity(commandBoundary.getCommandId());
		commandEntity.setCommandId(idEntity);
		
		commandEntity.setCommand(commandBoundary.getCommand());
		commandEntity.setInvocationTimestamp(commandBoundary.getInvocationTimestamp());
		
		UserIdEntity invokedBy = this.userIdConvertor.userIdBoundaryToUserIdEntity(
				commandBoundary.getInvokedBy().getUserId());
		commandEntity.setInvokedBy(invokedBy);
		
		SuperAppObjectIdEntity targetObject = this.superAppObjectIdConverter
				.superAppObjectIdBoundaryToSuperAppObjectIdEntity(
				commandBoundary.getTargetObject().getObjectId());
		commandEntity.setTargetObject(targetObject);
		
		commandEntity.setCommandAttributes(commandBoundary.getCommandAttributes());
		
		return commandEntity;
	}
	
	public MiniAppCommandBoundary miniAppCommandEntityToMiniAppCommandBoundary(MiniAppCommandEntity commandEntity) {
		MiniAppCommandBoundary commandBoundary = new MiniAppCommandBoundary();
		
		MiniAppCommandIdBoundary idBoundary = this.miniAppCommandIdConverter
				.miniAppCommandIdEntityToCommandIdBoundary(commandEntity.getCommandId());
		commandBoundary.setCommandId(idBoundary);
		
		commandBoundary.setCommand(commandEntity.getCommand());
		commandBoundary.setInvocationTimestamp(commandEntity.getInvocationTimestamp());
		
		UserIdBoundary invokedBy = this.userIdConvertor.userIdEntityToUserIdBoundary(
				commandEntity.getInvokedBy());
		CommandInvokerBoundary commandInvoker = new CommandInvokerBoundary();
		commandInvoker.setUserId(invokedBy);
		commandBoundary.setInvokedBy(commandInvoker);
		
		SuperAppObjectIdBoundary targetObject = this.superAppObjectIdConverter
				.superAppObjectEntityToSuperAppObjectBoundary(commandEntity.getTargetObject());
		SuperAppObjectTargeterBoundary objectTargeter = new SuperAppObjectTargeterBoundary();
		objectTargeter.setObjectId(targetObject);
		commandBoundary.setTargetObject(objectTargeter);
		
		commandBoundary.setCommandAttributes(commandEntity.getCommandAttributes());
		
		return commandBoundary;
	}
}
