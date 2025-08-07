package org.netway.dongnehankki.store.domain;

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
@NoArgsConstructor
public class Menu extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long menuId;

	private String name;

	private String description;

	private String image;

	private Integer price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="store_id", nullable = false)
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private Menu(String name, String description, String image, Integer price, Store store, User user) {
		this.store = store;
		this.name = name;
		this.description = description;
		this.image = image;
		this.price = price;
		this.user = user;
	}

	public static Menu createMenu(String name, String description, String image, Integer price, Store store, User user) {
		return new Menu(name, description, image, price, store, user);
	}
}
