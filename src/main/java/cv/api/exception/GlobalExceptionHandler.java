package cv.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLSyntaxErrorException;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({ SQLSyntaxErrorException.class, DataAccessException.class })
    public void handleException(Exception ex) {
        // Send email with error detail
        log.error("GlobalExceptionHandler : " + ex.getMessage());
    }

}
