package org.netway.dongnehankki.user.fixture;

import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.user.domain.User;

public class OwnerUserFixture {

    // Test시 사용하는 가짜 User Entity
    public static User get(String loginId, String password, String name, String phoneNumber, Store store){
        return User.ofOwner(loginId, password, "테스트사장님", name, phoneNumber, store);
    }

}
