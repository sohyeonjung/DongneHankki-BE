package org.netway.dongnehankki.analytics.dto.response;

import java.time.DayOfWeek;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WeeklyHourlyStatsResponse {

    private final DayOfWeek dayOfWeek;
    private final List<HourlyStat> hourlyStats;
}