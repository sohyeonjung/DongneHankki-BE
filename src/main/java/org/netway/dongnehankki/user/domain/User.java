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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	private String id;

	private String password;

	private String nickname;

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

	private User(String id, String password, String nickname, Role role, Store store) {
		this.id = id;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.store = store;
	}

	public static User ofCustomer(String id, String password, String nickname){
		return new User(id, password, nickname, Role.CUSTOMER, null);
	}

	public static User ofOwner(String id, String password, String nickname, Store store){
		return new User(id, password, nickname, Role.OWNER, store);
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updatePassword(String password) {
		this.password = password;
	}
}
