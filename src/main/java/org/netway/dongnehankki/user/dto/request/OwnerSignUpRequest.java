package org.netway.dongnehankki.user.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerSignUpRequest {
    private String loginId;
    private String password;
    private String name;
    private String phoneNumber;
    private Long storeId;
    private LocalDate birth;
}
