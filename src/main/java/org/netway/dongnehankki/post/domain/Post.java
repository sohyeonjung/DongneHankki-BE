package org.netway.dongnehankki.post.domain;

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
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postId;

	private String title;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="store_id")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;

	@OneToMany(mappedBy = "post")
	private List<Image> images= new ArrayList<>();

	@OneToMany(mappedBy = "post")
	private List<PostHashtag> postHashtags = new ArrayList<>();

	@OneToMany(mappedBy = "post")
	private List<Comment> comments = new ArrayList<>();
}
