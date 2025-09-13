package org.netway.dongnehankki.analytics.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class HourlyStat {

    private final int hour;
    private final long viewStoreCount;
    private final long viewPostCount;

    @Builder
    public HourlyStat(int hour, long viewStoreCount, long viewPostCount) {
        this.hour = hour;
        this.viewStoreCount = viewStoreCount;
        this.viewPostCount = viewPostCount;
    }
}