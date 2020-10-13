package com.tnt.authjwt.repo;

import com.tnt.authjwt.model.SomeResource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SomeResourceRepository extends PagingAndSortingRepository<SomeResource, Long> {
}
