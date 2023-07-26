package superapp.data.crud;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

import superapp.data.UserEntity;
import superapp.data.UserIdEntity;

public interface UserCrud extends ListCrudRepository<UserEntity, UserIdEntity> {
	public List<UserEntity> findAll(Pageable pageable);

}
