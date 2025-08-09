package org.netway.dongnehankki.post.dto.response;

import java.util.List;

public record CursorResult<T>(
    List<T> values,
    Long nextCursor
) {
}
