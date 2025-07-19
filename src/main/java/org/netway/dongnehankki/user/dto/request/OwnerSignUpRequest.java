package org.netway.dongnehankki.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerSignUpRequest {
    private String loginId;
    private String password;
    private String nickname;
    private String name;
    private String phoneNumber;
    private Long storeId;
}
