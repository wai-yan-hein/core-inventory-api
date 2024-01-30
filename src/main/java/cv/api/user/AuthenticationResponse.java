package cv.api.user;

import lombok.Data;

@Data
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpired;
    private Long refreshTokenExpired;
    private Integer macId;
}
