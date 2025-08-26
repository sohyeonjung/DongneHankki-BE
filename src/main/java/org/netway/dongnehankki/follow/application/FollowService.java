package org.netway.dongnehankki.follow.application;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.follow.domain.Follow;
import org.netway.dongnehankki.follow.exception.AlreadyFollowedException;
import org.netway.dongnehankki.follow.exception.NotFollowedException;
import org.netway.dongnehankki.follow.repository.FollowRepository;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void follow(Long userId, Long storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UnregisteredUserException::new);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(UnregisteredStoreException::new);

        followRepository.findByUser_UserIdAndStore_StoreId(userId, storeId)
                .ifPresent(follow -> {
                    throw new AlreadyFollowedException();
                });

        Follow follow = Follow.of(user, store);
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long userId, Long storeId) {
        Follow follow = followRepository.findByUser_UserIdAndStore_StoreId(userId, storeId)
                .orElseThrow(() -> new NotFollowedException());
        followRepository.delete(follow);
    }
}
