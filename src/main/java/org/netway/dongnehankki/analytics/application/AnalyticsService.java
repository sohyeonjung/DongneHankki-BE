package org.netway.dongnehankki.analytics.application;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.analytics.domain.ActivityLog;
import org.netway.dongnehankki.analytics.domain.ActivityType;
import org.netway.dongnehankki.analytics.dto.response.HourlyStat;
import org.netway.dongnehankki.analytics.dto.response.WeeklyHourlyStatsResponse;
import org.netway.dongnehankki.analytics.repository.ActivityLogRepository;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ActivityLogRepository activityLogRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void logActivity(User user, Store store, ActivityType activityType, Long targetId) {
        ActivityLog activityLog = ActivityLog.builder()
                .user(user)
                .store(store)
                .activityType(activityType)
                .targetId(targetId)
                .build();
        activityLogRepository.save(activityLog);
    }

    @Transactional(readOnly = true)
    public List<WeeklyHourlyStatsResponse> getWeeklyHourlyStats(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(UnregisteredStoreException::new);
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);

        List<Object[]> rawCounts = activityLogRepository.findWeeklyHourlyActivityCounts(store, startDate);

        // Map<DayOfWeek, Map<Hour, Map<ActivityType, Count>>>
        Map<DayOfWeek, Map<Integer, Map<ActivityType, Long>>> statsMap = new EnumMap<>(DayOfWeek.class);

        for (Object[] row : rawCounts) {
            DayOfWeek dayOfWeek = DayOfWeek.of(((Number) row[0]).intValue() % 7 + 1); // Adjust for DB (e.g., Sunday=1)
            int hour = ((Number) row[1]).intValue();
            ActivityType type = (ActivityType) row[2];
            Long count = (Long) row[3];

            statsMap.computeIfAbsent(dayOfWeek, k -> new HashMap<>())
                .computeIfAbsent(hour, k -> new EnumMap<>(ActivityType.class))
                .put(type, count);
        }

        List<WeeklyHourlyStatsResponse> result = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            Map<Integer, Map<ActivityType, Long>> hourlyData = statsMap.getOrDefault(day, new HashMap<>());

            List<HourlyStat> hourlyStats = IntStream.range(0, 24)
                .mapToObj(hour -> {
                    Map<ActivityType, Long> counts = hourlyData.getOrDefault(hour, new EnumMap<>(ActivityType.class));
                    return HourlyStat.builder()
                        .hour(hour)
                        .viewStoreCount(counts.getOrDefault(ActivityType.VIEW_STORE, 0L))
                        .viewPostCount(counts.getOrDefault(ActivityType.VIEW_POST, 0L))
                        .build();
                })
                .collect(Collectors.toList());

            result.add(new WeeklyHourlyStatsResponse(day, hourlyStats));
        }

        return result;
    }
}
