package org.netway.dongnehankki.global.auth;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UnregisteredUserException {
        return userRepository.findByLoginId(username)
                .map(CustomUserDetails::new)
                .orElseThrow(UnregisteredUserException::new);
    }
}
