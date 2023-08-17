package cv.api.message;

import cv.api.common.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
@Slf4j
public class MessageProcessor {
    private final List<Consumer<Message>> listeners = new CopyOnWriteArrayList<>();

    public void register(Consumer<Message> listener) {
        listeners.add(listener);
        log.info("Message listener add.");
    }

    public void process(Message gl) {
        listeners.forEach(c -> c.accept(gl));
    }
}