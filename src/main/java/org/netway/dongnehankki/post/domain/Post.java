package org.netway.dongnehankki.post.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.netway.dongnehankki.global.common.BaseEntity;
import org.netway.dongnehankki.store.domain.Store;
import org.hibernate.annotations.Where;
import org.netway.dongnehankki.user.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.netway.dongnehankki.user.domain.User.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at is NULL")
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postId;


	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="store_id")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images= new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PostHashtag> postHashtags = new HashSet<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PostLike> postLikes = new HashSet<>();

	@Enumerated(EnumType.STRING)
	private Role role;

	public enum Role {
		OWNER, CUSTOMER
	}


	public static Post createPost(String content, Store store, User user, Role role) {
		return new Post(content, store, user, role);
	}

	private Post(String content, Store store, User user, Role role) {
		this.content = content;
		this.store = store;
		this.user = user;
		this.role = role;
	}

	public void addImage(String url, int displayOrder) {
		Image image = new Image(url, this, displayOrder);
		this.images.add(image);
	}

	public void addPostHashtag(Hashtag hashtag) {
		PostHashtag postHashtag = new PostHashtag(this, hashtag);
		this.postHashtags.add(postHashtag);
	}

	public void update(String content, List<Hashtag> newHashtags) {
		this.content = content;
		this.postHashtags.clear();
		newHashtags.forEach(this::addPostHashtag);
	}
}
