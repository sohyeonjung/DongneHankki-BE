package org.netway.dongnehankki.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerSignUpRequest {
    private String id;
    private String password;
    private String nickname;
    private Long storeId;
}
