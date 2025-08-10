package org.netway.dongnehankki.store.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
@Embeddable
public class OperatingHour {
	@Enumerated(EnumType.STRING)
	private DayOfWeek dayOfWeek;

	private LocalTime openTime;

	private LocalTime closeTime;
}
