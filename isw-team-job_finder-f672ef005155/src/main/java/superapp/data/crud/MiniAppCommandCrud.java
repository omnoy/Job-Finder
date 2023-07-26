package superapp.data.crud;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

import superapp.data.MiniAppCommandEntity;
import superapp.data.MiniAppCommandIdEntity;

/**
 * Interface for storing commands in MongoDB
 * 
 * @author 	Rom Gat
 * 			Dori Rozen
 */
public interface MiniAppCommandCrud extends ListCrudRepository<MiniAppCommandEntity, MiniAppCommandIdEntity>{
	public List<MiniAppCommandEntity> findAll(Pageable pageable);
	
}
