package cv.api.controller;

import cv.api.common.Message;
import cv.api.message.MessageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageProcessor processor;

    @PostMapping("/send")
    public Mono<?> send(@RequestBody Message gl) {
        processor.process(gl);
        return Mono.just("sent");
    }

    @GetMapping(path = "/receive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> receive() {
        return Flux.create(sink -> processor.register(sink::next));
    }
}
