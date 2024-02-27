package cv.api.controller;

import cv.api.common.Message;
import cv.api.message.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageProcessor processor;

    @PostMapping("/send")
    public Mono<?> send(@RequestBody Message message) {
        processor.process(message);
        return Mono.just("sent");
    }

    @GetMapping(path = "/receive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> receive(@RequestParam String messageId) {
        return Flux.create(sink -> processor.register(messageId, sink::next));
    }
}
