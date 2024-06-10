package cv.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class GlobalWebExceptionHandler {

    @Bean
    public WebExceptionHandler webExceptionHandler() {
        return (exchange, ex) -> {
            if (ex instanceof ResponseStatusException) {
                log.error("webExceptionHandler : {}", ex.getMessage());
                return Mono.error(ex);
            }
            return handleOtherExceptions(exchange, ex);
        };
    }

    private Mono<Void> handleOtherExceptions(ServerWebExchange exchange, Throwable ex) {
        log.error("handleOtherExceptions : " + ex.getMessage());
        // Handle other exceptions
        // You can log the exception and return a specific error response
        return Mono.fromRunnable(() -> {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            // Set a custom response here if needed
        });
    }
}
