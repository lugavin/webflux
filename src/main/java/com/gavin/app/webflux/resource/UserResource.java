package com.gavin.app.webflux.resource;

import com.gavin.app.webflux.model.User;
import com.gavin.app.webflux.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;
import java.util.UUID;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/users")
public class UserResource {

    private final UserRepository userRepository;

    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public Mono<User> createUser(@Valid @RequestBody User user) {
        user.setUid(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    @DeleteMapping("/{uid}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String uid) {
        return userRepository.findById(uid)
                .flatMap(existUser ->
                        userRepository.delete(existUser)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{uid}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable String uid,
                                                 @Valid @RequestBody User user) {
        return userRepository.findById(uid).flatMap(existUser -> {
            existUser.setLogin(user.getLogin());
            existUser.setEmail(user.getEmail());
            existUser.setPhone(user.getPhone());
            existUser.setUpdatedAt(new Date());
            return userRepository.save(existUser);
        }).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{uid}")
    public Mono<ResponseEntity<User>> getUser(@PathVariable String uid) {
        return userRepository.findById(uid)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 一次性返回多条数据
     */
    @GetMapping
    public Flux<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * 以流的方式返回数据
     */
    @GetMapping(value = "stream", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getUsersStream() {
        return userRepository.findAll();
    }

}
