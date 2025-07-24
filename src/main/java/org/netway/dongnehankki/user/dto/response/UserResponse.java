package org.netway.dongnehankki.user.dto.response;

import lombok.AllArgsConstructor;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.domain.User.Role;


@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String loginId;
    private String nickname;
    private String name;
    private String phoneNumber;
    private Role role;
    private Long storeId;

    public Long getUserId() { return userId; }
    public String getLoginId() { return loginId; }
    public String getNickname() { return nickname; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public Role getRole() { return role; }
    public Long getStoreId() { return storeId; }


    public static UserResponse fromEntity(User user){
        Long userStoreId = null;

        if (user.getRole() == Role.OWNER && user.getStore() != null){
            userStoreId = user.getStore().getStoreId();
        }

        return new UserResponse(
            user.getUserId(),
            user.getLoginId(),
            user.getNickname(),
            user.getName(),
            user.getPhoneNumber(),
            user.getRole(),
            userStoreId
        );
    }
}
