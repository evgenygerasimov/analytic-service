package org.site.analyticservice.entity;

public record PageViewStat(
        String visitTime,
        int totalViews,
        int uniqueVisitors
) {
}