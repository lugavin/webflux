package com.gavin.app.webflux.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@Slf4j
@RestController
public class ReactorResource {

    @GetMapping("webmvc")
    public String webmvc() {
        long startTime = System.currentTimeMillis();
        String currTime = this.handle();
        long endTime = System.currentTimeMillis();
        log.info("WebMVC >> Execute completed in {} ms", endTime - startTime);
        return currTime;
    }

    @GetMapping("webflux")
    public Mono<String> webflux() {
        long startTime = System.currentTimeMillis();
        Mono<String> mono = Mono.fromSupplier(this::handle); // 中间操作, 没有执行最终操作, 不会阻塞
        long endTime = System.currentTimeMillis();
        log.info("WebFlux >> Execute completed in {} ms", endTime - startTime);
        return mono;
    }

    @GetMapping(value = "stream", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> stream() {
        long startTime = System.currentTimeMillis();
        Flux<Integer> flux = Flux.fromStream(IntStream.rangeClosed(1, 10).mapToObj(i -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            return i;
        }));
        long endTime = System.currentTimeMillis();
        log.info("Stream >> Execute completed in {} ms", endTime - startTime);
        return flux;
    }

    private String handle() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ignored) {
        }
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
