package cv.api.common;

import lombok.Data;

@Data
public class ApiError {
    private String error;
    private String message;
    public ApiError(String message) {
        this.message = message;
    }
}
