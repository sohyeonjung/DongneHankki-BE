package org.netway.dongnehankki.analytics.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.analytics.application.AnalyticsService;
import org.netway.dongnehankki.analytics.application.AnalyticsServiceImpl;
import org.netway.dongnehankki.analytics.dto.response.WeeklyHourlyStatsResponse;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "통계", description = "통계 관련 API")
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "주간 시간대별 가게 활동 통계", description = "최근 한 달간의 데이터를 기반으로 요일 및 시간대별 활동 통계를 조회합니다.")
    @GetMapping("/stores/{storeId}/weekly-hourly-stats")
    public ResponseEntity<ApiResponse<List<WeeklyHourlyStatsResponse>>> getWeeklyHourlyStats(
        @Parameter(description = "가게 ID") @PathVariable Long storeId
    ) {
        List<WeeklyHourlyStatsResponse> response = analyticsService.getWeeklyHourlyStats(storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "마케팅 분석 리포트 생성", description = "가게 활동 통계와 추가 데이터를 기반으로 AI 마케팅 분석 리포트를 생성합니다.")
    @GetMapping("/stores/{storeId}/marketing-report")
    public ResponseEntity<ApiResponse<String>> generateMarketingReport(
        @Parameter(description = "가게 ID") @PathVariable Long storeId
    ) {
        String report = analyticsService.generateMarketingReport(storeId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}
