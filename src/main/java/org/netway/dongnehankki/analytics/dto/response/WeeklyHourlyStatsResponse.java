package org.netway.dongnehankki.analytics.dto.response;

import java.time.DayOfWeek;
import java.util.List;

public record WeeklyHourlyStatsResponse(
    DayOfWeek dayOfWeek,
    List<HourlyStat> hourlyStats
) {
}
