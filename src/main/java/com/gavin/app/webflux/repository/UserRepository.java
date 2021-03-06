package com.gavin.app.webflux.repository;

import com.gavin.app.webflux.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, Long> {

    Mono<User> findByLogin(String login);

}