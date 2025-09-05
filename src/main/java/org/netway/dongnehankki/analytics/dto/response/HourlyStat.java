package org.netway.dongnehankki.analytics.dto.response;

import lombok.Builder;

public record HourlyStat(
    int hour,
    long viewStoreCount,
    long viewPostCount
) {
    @Builder
    public HourlyStat {
    }
}
