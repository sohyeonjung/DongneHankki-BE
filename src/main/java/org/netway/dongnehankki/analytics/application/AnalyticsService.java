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
            DayOfWeek dayOfWeek = DayOfWeek.of(((Number) row[0]).intValue() == 1 ? 7 : ((Number) row[0]).intValue() - 1); // Adjust for DB (e.g., Sunday=1 to Java Sunday=7)
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
            store.getIndustryCode(),
            weeklyHourlyStats,
            csvContent
        );

        return vertexAIService.generateText(prompt);
    }

    private String buildMarketingReportPrompt(
        String storeName,
        String storeAddress,
        Integer industryCode,
        List<WeeklyHourlyStatsResponse> weeklyHourlyStats,
        String csvContent
    ) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("""
        너는 전문적인 마케팅 분석가이자 컨설턴트야.
        주어진 가게 정보, 고객 활동 통계, 추가 데이터를 바탕으로 마케팅 분석 리포트를 작성해 줘.
        리포트는 다음 형식에 맞춰 한국어로 작성해야 해. 모든 [ ] 안의 내용은 데이터 기반으로 채워야 해.
        추가로 알려준 CSV 파일도 참고해서 데이터 작성해줘
        아래와 같은 내용을 참고해서 너무 길게는 말고 400자 내로 작성해줘
        아래의 내용을 무조건 사용할 필요는 없고 해당 가게 유형에 맞는 추천 문구를 작성해줘
        가게 유형은 아래에 적혀있어 
        아무런 마크다운 양식도 추가하지말고 그냥 문자열로만 리턴해 "\\n","\\n*", "\\n1" 이런 거도 적지마 아무런 이런문자 넣지마. 단순히 문장. 줄바꿈 개행문자 이런거 절대 넣지마 무조건 그냥 한글 문장으로만
        그냥 단순히 리포트 본문내용만 리턴해
        
        
        
        최근 한 달간 고객들의 [가게명] 활동 데이터를 분석한 결과, 특정 요일과 시간대에 고객들의 관심이 집중되는 경향을 보였습니다. 이 리포트를 통해 고객들이 가장 활발하게 활동하는 시점을
        파악하고, 마케팅 효율을 높여보세요!

        1. 마케팅 활용 제안


        * 최적의 게시물 업로드 시간:
            * [최고 피크 시간대]에 맞춰 새로운 메뉴 사진, 이벤트 공지, 스토리 등을 게시하여 노출을 극대화하세요.
            * [예: 금요일 저녁 7시]는 주말 계획을 세우는 고객들에게 [가게명]을 각인시킬 좋은 기회입니다.


        * 콘텐츠 전략 차별화:
            * 가게 조회수가 높은 시간대([예: 주중 점심시간])에는 메뉴 정보, 영업시간, 위치 등 실용적인 정보를 강조하는 콘텐츠를 게시하세요.
            * 게시글 조회수가 높은 시간대([예: 저녁 늦은 시간])에는 음식 사진, 분위기, 고객 후기 등 시각적이고 감성적인 콘텐츠를 집중적으로 노출하여 고객의 흥미를 유발하세요.


        * 비활성 시간대 활용:
            * 상대적으로 고객 활동이 적은 시간대([예: 평일 오전])에는 특정 고객층을 위한 게릴라성 이벤트([예: '오전 방문 고객 아메리카노 할인', '디저트메뉴추천', '야식 메뉴 추천', '틈새 이벤트'])나  등 틈새 마케팅을 시도해 볼 수 있습니다.
            위 내용을 무조건 사용할 필요는 없고 해당 가게 유형에 맞을만한 추천 문구를 작성해줘
        ---
        [추가 정보]
        가게 이름: """).append(storeName).append("""
        가게 주소: """).append(storeAddress).append("""
        고객 활동 통계 (요일별 시간대별 조회수):
        """).append(weeklyHourlyStats.stream().map(WeeklyHourlyStatsResponse::toString).collect(Collectors.joining("\n"))).append("""
        추가 데이터 (CSV 내용):
        """).append(csvContent).append("""
        **[참고 가게 종류 표]**
            | 업종명 | 업종코드 |
            | --- | --- |
            | (축산물, 정육점/축산물, 정육점) | 2102 |
            | (수산물, 건어물/수산물, 건어물) | 2103 |
            | (농산물, 청과물/농산물, 청과물) | 2104 |
            | (기타식음료품/기타식음료품) | 2105 |
            | (건강보조식품/건강보조식품) | 2201 |
            | (일반음식점/일반음식점) | 2301 |
            | (중식전문점/중식전문점) | 2302 |
            | (일식전문점/일식전문점) | 2303 |
            | (서양식전문점/서양식전문점) | 2305 |
            | (치킨전문점/치킨전문점) | 2309 |
            | (식음료(기타)/식음료(기타)) | 2310 |
            | (제과, 제빵/제과, 제빵) | 2501 |
            | (커피전문점/커피전문점 | 2502 |
            | (일반주점/일반주점) | 2601 |
            | (슈퍼마켓, 마트/슈퍼마켓, 마트) | 5201 |
            | (편의점/편의점) | 5202 |
        
        """).append(industryCode);

        return promptBuilder.toString();
    }
}
