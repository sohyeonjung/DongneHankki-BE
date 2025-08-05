package org.netway.dongnehankki.post.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag {

	public static Hashtag of(String name) {
		return new Hashtag(null, name, new ArrayList<>());
	}

	private Hashtag(Long hashtagId, String name, List<PostHashtag> postHashtags) {
		this.hashtagId = hashtagId;
		this.name = name;
		this.postHashtags = postHashtags;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long hashtagId;

	private String name;

	@OneToMany(mappedBy = "hashtag")
	private List<PostHashtag> postHashtags = new ArrayList<>();
}
