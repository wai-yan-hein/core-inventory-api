package cv.api.user;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Builder
public class AuthenticationRequest {

    private String serialNo;
    private String password;
    private String programName;



}
