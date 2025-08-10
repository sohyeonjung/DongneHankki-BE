package org.netway.dongnehankki.store.dto.request;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateStoreOperatingHoursRequest {
	@NotNull
	@Size(min = 7, max = 7, message = "운영시간은 반드시 7개의 요일 정보가 필요합니다.")
	private List<OperatingHourRequest> operatingHours;

	@Builder
	@Getter
	public static class OperatingHourRequest {
		@NotNull
		private DayOfWeek dayOfWeek;
		private LocalTime openTime;
		private LocalTime closeTime;
	}
}
