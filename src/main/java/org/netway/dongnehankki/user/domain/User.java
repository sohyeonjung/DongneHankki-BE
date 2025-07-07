package org.netway.dongnehankki.user.domain;

import java.util.ArrayList;
import java.util.List;

import org.netway.dongnehankki.global.common.BaseEntity;
import org.netway.dongnehankki.follow.domain.Follow;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.store.domain.Store;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	private String id;

	private String password;

	private String nickname;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToOne(optional = false)
	@JoinColumn(name = "store_id")
	private Store store;

	@OneToMany(mappedBy = "user")
	private List<Follow> follows = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Post> posts = new ArrayList<>();

	enum Role {
		OWNER, CUSTOMER, ADMIN
	}
}
