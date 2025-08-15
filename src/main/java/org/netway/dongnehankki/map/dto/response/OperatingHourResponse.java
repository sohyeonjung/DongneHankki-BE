package org.netway.dongnehankki.map.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.netway.dongnehankki.store.domain.OperatingHour;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperatingHourResponse {
	private DayOfWeek dayOfWeek;
	private LocalTime openTime;
	private LocalTime closeTime;

	public static OperatingHourResponse fromEntity(OperatingHour operatingHour) {
		return OperatingHourResponse.builder()
			.dayOfWeek(operatingHour.getDayOfWeek())
			.openTime(operatingHour.getOpenTime())
			.closeTime(operatingHour.getCloseTime())
			.build();
	}
}
