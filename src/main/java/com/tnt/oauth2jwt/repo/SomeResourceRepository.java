package com.tnt.oauth2jwt.repo;

import com.tnt.oauth2jwt.model.SomeResource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SomeResourceRepository extends PagingAndSortingRepository<SomeResource, Long> {
}
