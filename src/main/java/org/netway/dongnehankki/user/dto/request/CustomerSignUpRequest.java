package org.netway.dongnehankki.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerSignUpRequest {
    private String loginId;
    private String password;
    private String nickname;
}
