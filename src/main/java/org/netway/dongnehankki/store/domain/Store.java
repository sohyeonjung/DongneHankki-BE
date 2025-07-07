package org.netway.dongnehankki.store.domain;

import java.util.ArrayList;
import java.util.List;

import org.netway.dongnehankki.global.common.BaseEntity;
import org.netway.dongnehankki.follow.domain.Follow;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.user.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Store extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long storeId;

	private String name;

	private Double latitude;

	private Double longitude;

	private Integer likeCount;

	private String address;

	@OneToOne(mappedBy = "store")
	private User user;

	@OneToMany(mappedBy = "store")
	private List<Menu> menus = new ArrayList<>();

	@OneToMany(mappedBy = "store")
	private List<Post> posts = new ArrayList<>();

	@OneToMany(mappedBy = "store")
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "store")
	private List<Follow> follows = new ArrayList<>();
}
