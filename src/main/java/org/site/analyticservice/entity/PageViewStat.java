package org.site.analyticservice.entity;

public record PageViewStat(
        String visitTime,           // например: "2025-05-21 15:00"
        int totalViews,        // общее число просмотров
        int uniqueVisitors     // уникальные сессии
) {
}