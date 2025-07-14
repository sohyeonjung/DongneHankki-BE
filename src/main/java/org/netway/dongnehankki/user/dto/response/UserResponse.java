package org.netway.dongnehankki.user.dto.response;

import lombok.AllArgsConstructor;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.domain.User.Role;


@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String id;
    private String nickname;
    private Role role;
    private Long storeId;

    public Long getUserId() { return userId; }
    public String getId() { return id; }
    public String getNickname() { return nickname; }
    public Role getRole() { return role; }
    public Long getStoreId() { return storeId; }


    public static UserResponse fromEntity(User user){
        Long userStoreId = null;

        if (user.getRole() == Role.OWNER && user.getStore() != null){
            userStoreId = user.getStore().getStoreId();
        }

        return new UserResponse(
            user.getUserId(),
            user.getId(),
            user.getNickname(),
            user.getRole(),
            userStoreId
        );
    }
}
