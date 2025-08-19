package org.netway.dongnehankki.follow.domain;

import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.user.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Follow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long followId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="store_id", nullable = false)
	private Store store;

	public static Follow of(User user, Store store) {
		Follow follow = new Follow();
		follow.user = user;
		follow.store = store;
		return follow;
	}
}
