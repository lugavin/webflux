package com.gavin.app.webflux.resource;

import com.gavin.app.webflux.model.User;
import com.gavin.app.webflux.repository.UserRepository;
import com.gavin.app.webflux.util.SnowflakeIdWorker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

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
        user.setUid(SnowflakeIdWorker.getInstance().nextId());
        return userRepository.save(user);
    }

    @DeleteMapping("/{uid}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long uid) {
        return userRepository.findById(uid)
                .flatMap(existUser ->
                        userRepository.delete(existUser)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{uid}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable Long uid,
                                                 @Valid @RequestBody User user) {
        return userRepository.findById(uid).flatMap(existUser -> {
            existUser.setLogin(user.getLogin());
            existUser.setEmail(user.getEmail());
            existUser.setPhone(user.getPhone());
            existUser.setUpdatedAt(new Date());
            existUser.setUpdatedBy(user.getUpdatedBy());
            return userRepository.save(existUser);
        }).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{uid}")
    public Mono<ResponseEntity<User>> getUser(@PathVariable Long uid) {
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
