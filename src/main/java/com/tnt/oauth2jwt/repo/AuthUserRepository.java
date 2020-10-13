package com.tnt.oauth2jwt.repo;

import com.tnt.oauth2jwt.model.AuthUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends PagingAndSortingRepository<AuthUser, Long> {
	Optional<AuthUser> findOneByUsername(String username);
}
