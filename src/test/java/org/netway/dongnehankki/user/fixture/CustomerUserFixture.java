package org.netway.dongnehankki.user.fixture;

import org.netway.dongnehankki.user.domain.User;

public class CustomerUserFixture {

    // Test시 사용하는 가짜 User Entity
    public static User get(String loginId, String password, String nickname, String name, String phoneNumber){
        return User.ofCustomer(loginId, password, nickname, name, phoneNumber);
    }

}
