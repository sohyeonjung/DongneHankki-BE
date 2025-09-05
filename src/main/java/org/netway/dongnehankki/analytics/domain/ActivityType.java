package org.netway.dongnehankki.analytics.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityType {
    VIEW_STORE("가게 상세 조회"),
    VIEW_POST("게시글 조회");

    private final String description;
}
