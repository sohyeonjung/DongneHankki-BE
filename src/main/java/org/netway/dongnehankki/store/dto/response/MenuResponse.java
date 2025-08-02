package org.netway.dongnehankki.store.dto.response;

import org.netway.dongnehankki.store.domain.Menu;

import lombok.Builder;

@Builder
public class MenuResponse {
	private Long menuId;
	private String name;
	private String description;
	private String image;
	private Integer price;

	public static MenuResponse fromEntity(Menu menu) {
		return MenuResponse.builder()
			.menuId(menu.getMenuId())
			.name(menu.getName())
			.description(menu.getDescription())
			.image(menu.getImage())
			.price(menu.getPrice())
			.build();
	}
}
