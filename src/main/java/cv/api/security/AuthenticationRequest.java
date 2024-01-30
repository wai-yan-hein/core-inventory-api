package cv.api.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRequest {
  private String serialNo;
  private String password;
  private String programName;

}
