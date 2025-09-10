package org.netway.dongnehankki.analytics.application;

import java.util.List;
import org.netway.dongnehankki.analytics.domain.ActivityType;
import org.netway.dongnehankki.analytics.dto.response.WeeklyHourlyStatsResponse;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.user.domain.User;

public interface AnalyticsService {
    void logActivity(User user, Store store, ActivityType activityType, Long targetId);
    List<WeeklyHourlyStatsResponse> getWeeklyHourlyStats(Long storeId);
    String generateMarketingReport(Long storeId);
}
