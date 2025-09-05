package org.netway.dongnehankki.analytics.application;

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
import org.netway.dongnehankki.post.application.VertexAIService;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ActivityLogRepository activityLogRepository;
    private final StoreRepository storeRepository;
    private final VertexAIService vertexAIService;
    private final RestTemplate restTemplate;

    @Value("${analytics.csv}")
    private String csvFileUrl;

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

    public String fetchCsvContent(String csvFileUrl) {
        return restTemplate.getForObject(csvFileUrl, String.class);
    }

    @Transactional(readOnly = true)
    public String generateMarketingReport(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(UnregisteredStoreException::new);
        List<WeeklyHourlyStatsResponse> weeklyHourlyStats = getWeeklyHourlyStats(storeId);
        String csvContent = fetchCsvContent(this.csvFileUrl);

        String prompt = buildMarketingReportPrompt(
            store.getName(),
            store.getAddress(),
            weeklyHourlyStats,
            csvContent
        );

        return vertexAIService.generateText(prompt);
    }

    private String buildMarketingReportPrompt(
        String storeName,
        String storeAddress,
        List<WeeklyHourlyStatsResponse> weeklyHourlyStats,
        String csvContent
    ) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("""
        너는 전문적인 마케팅 분석가이자 컨설턴트야.
        주어진 가게 정보, 고객 활동 통계, 추가 데이터를 바탕으로 마케팅 분석 리포트를 작성해 줘.
        리포트는 다음 형식에 맞춰 한국어로 작성해야 해. 모든 [ ] 안의 내용은 데이터 기반으로 채워야 해.

        [가게명] 고객 활동 분석 리포트 (최근 한 달)

        요약:
        최근 한 달간 고객들의 [가게명] 활동 데이터를 분석한 결과, 특정 요일과 시간대에 고객들의 관심이 집중되는 경향을 보였습니다. 이 리포트를 통해 고객들이 가장 활발하게 활동하는 시점을
        파악하고, 마케팅 효율을 높여보세요!

        ---

        1. 주요 활동 시간대 (히트맵 참조)


        * 최고 피크 시간대:
            * 고객 활동이 가장 활발한 시간대는 [가장 높은 요일] [가장 높은 시간대] 입니다. (총 [해당 시간대 총 조회수]회: 가게 조회수 [가게 조회수]회, 게시글 조회수 [게시글 조회수]회)
            * 이 시간대는 고객들이 [가게명]에 가장 높은 관심을 보이는 시점이므로, 중요한 정보나 프로모션을 게시하기에 가장 적합합니다.


        * 요일별 특징:
            * 주중: 주로 [가장 높은 주중 요일]의 [가장 높은 주중 시간대]에 활동이 많았습니다. 점심시간 전후([예: 11시~13시])와 퇴근 후([예: 18시~20시])에 꾸준한 유입이 보입니다.
            * 주말: [가장 높은 주말 요일]의 [가장 높은 주말 시간대]에 활동이 집중됩니다. [예: 주말 점심/저녁 식사 계획을 세우는 시간대]로 보입니다.

        ---

        2. 마케팅 활용 제안


        * 최적의 게시물 업로드 시간:
            * [최고 피크 시간대]에 맞춰 새로운 메뉴 사진, 이벤트 공지, 스토리 등을 게시하여 노출을 극대화하세요.
            * [예: 금요일 저녁 7시]는 주말 계획을 세우는 고객들에게 [가게명]을 각인시킬 좋은 기회입니다.


        * 콘텐츠 전략 차별화:
            * 가게 조회수가 높은 시간대([예: 주중 점심시간])에는 메뉴 정보, 영업시간, 위치 등 실용적인 정보를 강조하는 콘텐츠를 게시하세요.
            * 게시글 조회수가 높은 시간대([예: 저녁 늦은 시간])에는 음식 사진, 분위기, 고객 후기 등 시각적이고 감성적인 콘텐츠를 집중적으로 노출하여 고객의 흥미를 유발하세요.


        * 비활성 시간대 활용:
            * 상대적으로 고객 활동이 적은 시간대([예: 평일 오전])에는 특정 고객층을 위한 게릴라성 이벤트([예: '오전 방문 고객 아메리카노 할인'])나 '야식 메뉴' 등 틈새 마케팅을 시도해 볼 수 있습니다.

        ---
        [추가 정보]
        가게 이름: """).append(storeName).append("""
        가게 주소: """).append(storeAddress).append("""
        고객 활동 통계 (요일별 시간대별 조회수):
        """).append(weeklyHourlyStats.stream().map(WeeklyHourlyStatsResponse::toString).collect(Collectors.joining("\n"))).append("""
        추가 데이터 (CSV 내용):
        """).append(csvContent).append("""
        * 혹시 너가 csv 파일을 읽고 데이터를 응답한거라면 csv 데이터 읽었음 이라는 멘트를 마지막에 달아줘
        """);

        return promptBuilder.toString();
    }
}
