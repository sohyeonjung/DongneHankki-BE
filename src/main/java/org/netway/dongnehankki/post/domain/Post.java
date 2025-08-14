package org.netway.dongnehankki.post.domain;

import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

import org.netway.dongnehankki.global.common.BaseEntity;
import org.netway.dongnehankki.store.domain.Store;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
	private List<PostHashtag> postHashtags = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();


	public static Post createPost(String content, Store store, User user) {
		return new Post(content, store, user);
	}

	private Post(String content, Store store, User user) {
		this.content = content;
		this.store = store;
		this.user = user;
	}

	public void addImage(String url, int displayOrder) {
		Image image = new Image(url, this, displayOrder);
		this.images.add(image);
	}

	public void addPostHashtag(Hashtag hashtag) {
		PostHashtag postHashtag = new PostHashtag(this, hashtag);
		this.postHashtags.add(postHashtag);
	}
}
