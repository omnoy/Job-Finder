package superapp.data.crud;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import superapp.data.SuperAppObjectEntity;
import superapp.data.SuperAppObjectIdEntity;

public interface SuperAppObjectCrud extends ListCrudRepository<SuperAppObjectEntity, SuperAppObjectIdEntity> {
	// General:
	public List<SuperAppObjectEntity> findAll(Pageable pageable);

	public List<SuperAppObjectEntity> findAllByActiveIsTrue(Pageable pageable);

	public List<SuperAppObjectEntity> findAllByType(@Param("type") String type, Pageable pageable);

	public List<SuperAppObjectEntity> findAllByTypeAndActiveIsTrue(@Param("type") String type, Pageable pageable);

	public List<SuperAppObjectEntity> findAllByAlias(@Param("alias") String alias, Pageable pageable);

	public List<SuperAppObjectEntity> findAllByAliasAndActiveIsTrue(@Param("alias") String alias, Pageable pageable);

	public List<SuperAppObjectEntity> findAllByLocationNear(Point point, Distance dist, Pageable pageable);

	public List<SuperAppObjectEntity> findAllByLocationNearAndActiveIsTrue(Point point, Distance dist,
			Pageable pageable);

	public List<SuperAppObjectEntity> findAllByParentsContains(@Param("parents") SuperAppObjectEntity parent,
			Pageable pageable);

	public List<SuperAppObjectEntity> findAllByParentsContainsAndActiveIsTrue(
			@Param("parents") SuperAppObjectEntity parentId, Pageable pageable);

	public List<SuperAppObjectEntity> findAllByChildrenContains(@Param("children") SuperAppObjectEntity child,
			Pageable pageable);

	public List<SuperAppObjectEntity> findAllByChildrenContainsAndActiveIsTrue(@Param("children") SuperAppObjectEntity child,
			Pageable pageable);

	// General Add-ons:
    @Query("{ $and: [ {type : userRecord}, {'objectDetails.owner.userId.superapp' : ?0}, "
            + "{active: true}, {'objectDetails.owner.userId.email' : ?1} ] }")

	public List<SuperAppObjectEntity> getUserRecord(String superapp, String email);

	// JobsGatherer:
	@Query("{ $and: [ {type : jobListing}, {active: true}, {'objectDetails.tags' : { $in : ?0}} ] }")
	public List<SuperAppObjectEntity> findJobsByTags(List<String> tags);

	@Query("{ $and: [  {type : jobListing}, {'_id.internalObjectId' : { $in : ?0}}, "
			+ "{active: true}, {'objectDetails.tags' : { $in : ?1}} ] }")
	public List<SuperAppObjectEntity> findJobsByInternalIdsAndTags(List<String> internalIds, List<String> tags);

	// ResumeBuilder:
	@Query("{ $and: [ {type : resume}, {'objectDetails.status' : 'ready'}, {active: true}, "
			+ "{'objectDetails.requestingUser.superapp' : ?0}, {'objectDetails.requestingUser.email' : ?1} ] }")
	public List<SuperAppObjectEntity> find10LatestResumes(String superapp, String email, Pageable pageable);
}
