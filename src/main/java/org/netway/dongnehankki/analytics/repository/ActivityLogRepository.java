package org.netway.dongnehankki.analytics.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.netway.dongnehankki.analytics.domain.ActivityLog;
import org.netway.dongnehankki.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @Query("SELECT FUNCTION('DAYOFWEEK', a.createdAt), FUNCTION('HOUR', a.createdAt), a.activityType, COUNT(a) " +
        "FROM ActivityLog a " +
        "WHERE a.store = :store AND a.createdAt >= :startDate " +
        "GROUP BY FUNCTION('DAYOFWEEK', a.createdAt), FUNCTION('HOUR', a.createdAt), a.activityType")
    List<Object[]> findWeeklyHourlyActivityCounts(@Param("store") Store store, @Param("startDate") LocalDateTime startDate);
}
