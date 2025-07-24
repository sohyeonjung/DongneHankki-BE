package org.netway.dongnehankki.store.domain;

import org.netway.dongnehankki.global.common.BaseEntity;

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
}
