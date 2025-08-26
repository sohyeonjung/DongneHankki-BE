package org.netway.dongnehankki.user.domain;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	private String loginId;

	private String password;

	private String nickname;

	private String name;

	private String phoneNumber;

	private String fcmToken;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToOne(optional = true)
	@JoinColumn(name = "store_id")
	private Store store;

	@OneToMany(mappedBy = "user")
	private List<Follow> follows = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Post> posts = new ArrayList<>();

	public enum Role {
		OWNER, CUSTOMER, ADMIN
	}

	private User(String loginId, String password, String nickname, String name, String phoneNumber, Role role, Store store) {
		this.loginId = loginId;
		this.password = password;
		this.nickname = nickname;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.store = store;
	}

	public static User ofCustomer(String loginId, String password, String nickname, String name, String phoneNumber){
		return new User(loginId, password, nickname, name, phoneNumber, Role.CUSTOMER, null);
	}

	public static User ofOwner(String loginId, String password, String nickname, String name, String phoneNumber, Store store){
		return new User(loginId, password, nickname, name, phoneNumber, Role.OWNER, store);
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void updateFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}
}
