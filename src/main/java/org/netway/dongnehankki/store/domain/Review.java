package org.netway.dongnehankki.store.domain;

import org.hibernate.annotations.Where;
import org.netway.dongnehankki.global.common.BaseEntity;
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
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor
public class Review extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;

	private String content;

	private Integer scope;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	private Review(String content, Integer scope, User user, Store store) {
		this.content = content;
		this.scope = scope;
		this.user = user;
		this.store = store;
	}

	public static Review createReview(String content, Integer scope, User user, Store store) {
		return new Review(content, scope, user, store);
	}

	public Review updateReview(String content, Integer scope){
		this.content = content;
		this.scope = scope;
		return this;
	}
}
