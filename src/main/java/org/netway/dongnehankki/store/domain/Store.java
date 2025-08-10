package org.netway.dongnehankki.store.domain;

import java.time.LocalDateTime;
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

	private String sigun;

	private String address;

	private Integer industryCode;

	private Long businessRegistrationNumber;

	private Integer scope;

	private LocalDateTime openAt;

	private LocalDateTime closeAt;

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

	private Store(String name, Double latitude, Double longitude, String address, String sigun,
		Integer industryCode, Long businessRegistrationNumber) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.sigun = sigun;
		this.industryCode = industryCode;
		this.businessRegistrationNumber = businessRegistrationNumber;
	}

	public static Store createStore(String name, Double latitude, Double longitude, String address, String sigun,
	Integer industryCode, Long businessRegistrationNumber){
		return new Store(name, latitude, longitude, address, sigun, industryCode, businessRegistrationNumber);
	}

	public void updateStore(String cmpnmNm, Double latitude, Double longitude, String refineRoadnmAddr, String sigunNm, Integer indutypeCd) {
		this.name = cmpnmNm;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = refineRoadnmAddr;
		this.sigun = sigunNm;
		this.industryCode = indutypeCd;
	}
}
