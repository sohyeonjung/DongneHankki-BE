package org.netway.dongnehankki.store.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class OperatingHour {
	@Enumerated(EnumType.STRING)
	private DayOfWeek dayOfWeek;

	private LocalTime openTime;

	private LocalTime closeTime;

	private OperatingHour(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
		this.dayOfWeek = dayOfWeek;
		this.openTime = openTime;
		this.closeTime = closeTime;
	}

	public static OperatingHour createOperatingHour(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
		return new OperatingHour(dayOfWeek, openTime, closeTime);
	}
}
