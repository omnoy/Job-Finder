package superapp.logic.components;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import superapp.boundaries.SuperAppObjectBoundary;
import superapp.boundaries.sub_boundaries.SuperAppObjectIdBoundary;
import superapp.data.SuperAppObjectIdEntity;
import superapp.data.crud.SuperAppObjectCrud;
import superapp.logic.HTTPForbiddenException;
import superapp.logic.converters.SuperAppObjectConverter;
import superapp.logic.converters.SuperAppObjectIdConverter;


@Component
public class SuperAppObjectAuthenticationComponent {
	private SuperAppObjectCrud databaseSuperAppObjectCrud;
	private SuperAppObjectIdConverter superAppObjectIdConverter;
	private SuperAppObjectConverter superAppObjectConverter;
	
	public SuperAppObjectAuthenticationComponent(SuperAppObjectCrud databaseSuperAppObjectCrud) {
		this.databaseSuperAppObjectCrud = databaseSuperAppObjectCrud;
	}
	
	@PostConstruct
	void setup() {
		this.superAppObjectIdConverter = new SuperAppObjectIdConverter(); 
		this.superAppObjectConverter = new SuperAppObjectConverter();
	}
	
	/** This is a helper-method which returns a SuperAppObjectTarget by target object ID (If found, else throws 403 - Forbidden).
	 * 
	 * @param	objectId
	 * 			The target object Id.
	 * 
	 * @return	SuperApp object Boundary.
	 */
	public SuperAppObjectBoundary getSuperAppObjectBoundary(SuperAppObjectIdBoundary superAppObjectId) {
		SuperAppObjectIdEntity superAppObjectIdEntity = this.superAppObjectIdConverter
				.superAppObjectIdBoundaryToSuperAppObjectIdEntity(superAppObjectId); 
		
		return this.superAppObjectConverter.superAppObjectEntityToSuperAppObjectBoundary
				(this.databaseSuperAppObjectCrud
						.findById(superAppObjectIdEntity)
						.orElseThrow(() -> 
						new HTTPForbiddenException("SuperApp: " + superAppObjectIdEntity + " not registered")));
	}

}
