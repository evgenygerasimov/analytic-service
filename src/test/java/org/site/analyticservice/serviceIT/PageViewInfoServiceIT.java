package org.site.analyticservice.serviceIT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.site.analyticservice.TestContainerConfig;
import org.site.analyticservice.entity.PageViewEvent;
import org.site.analyticservice.entity.PageViewStat;
import org.site.analyticservice.repositiry.PageViewInfoRepository;
import org.site.analyticservice.service.PageViewInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PageViewInfoServiceIT extends TestContainerConfig {

    @Autowired
    private PageViewInfoService pageViewInfoService;

    @Autowired
    private PageViewInfoRepository pageViewInfoRepository;

    @BeforeEach
    void clearDb() {
        pageViewInfoRepository.deleteAll();
    }

    @Test
    void testSavePageViewEvent() {
        String json = """
                {
                  "visitTime": [2025, 6, 17, 14, 30, 0, 0],
                  "sessionId": "session123",
                  "path": "/home",
                  "ip": "192.168.1.1",
                  "userAgent": "Mozilla/5.0"
                }
                """;

        PageViewEvent saved = pageViewInfoService.save(json);

        assertThat(saved.getSessionId()).isEqualTo("session123");
        assertThat(saved.getPath()).isEqualTo("/home");
        assertThat(saved.getVisitTime()).isEqualTo(LocalDateTime.of(2025, 6, 17, 14, 30, 0, 0));
    }

    @Test
    void testGetPageViewStatsBetweenDates() {
        PageViewEvent view1 = PageViewEvent.builder()
                .sessionId("s1")
                .visitTime(LocalDateTime.of(2025, 6, 17, 14, 30, 0, 0))
                .path("/products")
                .ip("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

        PageViewEvent view2 = PageViewEvent.builder()
                .sessionId("s2")
                .visitTime(LocalDateTime.of(2025, 6, 18, 14, 30, 0, 0))
                .path("/about")
                .ip("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

        PageViewEvent view3 = PageViewEvent.builder()
                .sessionId("s1")
                .visitTime(LocalDateTime.of(2025, 6, 19, 14, 30, 0, 0))
                .path("/products")
                .ip("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

        pageViewInfoRepository.save(view1);
        pageViewInfoRepository.save(view2);
        pageViewInfoRepository.save(view3);

        LocalDate from = LocalDate.of(2025, 6, 17);
        LocalDate to = LocalDate.of(2025, 6, 18);

        List<PageViewStat> stats = pageViewInfoService.getPageViewStats(from, to);

        assertThat(stats).hasSize(2);

        assertThat(stats.get(0).visitTime()).isEqualTo("2025-06-17");
        assertThat(stats.get(0).totalViews()).isEqualTo(1);
        assertThat(stats.get(0).uniqueVisitors()).isEqualTo(1);

        assertThat(stats.get(1).visitTime()).isEqualTo("2025-06-18");
        assertThat(stats.get(1).totalViews()).isEqualTo(1);
        assertThat(stats.get(1).uniqueVisitors()).isEqualTo(1);
    }
}
