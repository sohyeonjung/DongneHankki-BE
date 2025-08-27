package org.netway.dongnehankki.user.infrastructure;

import java.util.List;
import java.util.Optional;
import org.netway.dongnehankki.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByNickname(String nickname);

    List<User> findByRole(User.Role role);
}
