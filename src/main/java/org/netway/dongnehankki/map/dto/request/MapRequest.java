package org.netway.dongnehankki.map.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MapRequest {

	@NotNull(message = "위도는 필수 값입니다.")
	@Min(value = -90, message = "위도는 -90보다 작을 수 없습니다.")
	@Max(value = 90, message = "위도는 90보다 클 수 없습니다.")
	private Double latitude;

	@NotNull(message = "경도는 필수 값입니다.")
	@Min(value = -180, message = "경도는 -180보다 작을 수 없습니다.")
	@Max(value = 180, message = "경도는 180보다 클 수 없습니다.")
	private Double longitude;

	@NotNull(message = "줌 레벨은 필수 값입니다.")
	private Integer zoomLevel;

	private Integer industryCode;

	private Integer scope;
}