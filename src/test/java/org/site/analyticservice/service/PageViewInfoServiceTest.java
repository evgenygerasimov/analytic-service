package org.site.analyticservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.analyticservice.entity.PageViewEvent;
import org.site.analyticservice.entity.PageViewStat;
import org.site.analyticservice.repositiry.PageViewInfoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageViewInfoServiceTest {

    @Mock
    private PageViewInfoRepository pageViewInfoRepository;

    @InjectMocks
    private PageViewInfoService pageViewInfoService;

    @Test
    void testSave_shouldDeserializeJsonAndSavePageViewEvent() {
        String json = """
                {
                  "pageViewEventId": "a3f79d0c-9b3f-4c84-9a2d-0c7c0a4e59b2",
                  "visitTime": [2025, 6, 17, 14, 30, 0, 0],
                  "sessionId": "session123",
                  "path": "/home",
                  "ip": "192.168.1.1",
                  "userAgent": "Mozilla/5.0"
                }
                """;

        PageViewEvent expectedEvent = new PageViewEvent();
        expectedEvent.setPageViewEventId(UUID.fromString("a3f79d0c-9b3f-4c84-9a2d-0c7c0a4e59b2"));
        expectedEvent.setVisitTime(LocalDateTime.of(2025, 6, 17, 14, 30));
        expectedEvent.setSessionId("session123");
        expectedEvent.setPath("/home");
        expectedEvent.setIp("192.168.1.1");
        expectedEvent.setUserAgent("Mozilla/5.0");

        when(pageViewInfoRepository.save(any(PageViewEvent.class))).thenReturn(expectedEvent);

        PageViewEvent saved = pageViewInfoService.save(json);

        ArgumentCaptor<PageViewEvent> captor = ArgumentCaptor.forClass(PageViewEvent.class);
        verify(pageViewInfoRepository, times(1)).save(captor.capture());

        PageViewEvent captured = captor.getValue();
        assertEquals(expectedEvent.getPageViewEventId(), captured.getPageViewEventId());
        assertEquals(expectedEvent.getVisitTime(), captured.getVisitTime());
        assertEquals(expectedEvent.getSessionId(), captured.getSessionId());
        assertEquals(expectedEvent.getPath(), captured.getPath());
        assertEquals(expectedEvent.getIp(), captured.getIp());
        assertEquals(expectedEvent.getUserAgent(), captured.getUserAgent());

        assertEquals(expectedEvent.getPageViewEventId(), saved.getPageViewEventId());
        assertEquals(expectedEvent.getVisitTime(), saved.getVisitTime());
        assertEquals(expectedEvent.getSessionId(), saved.getSessionId());
        assertEquals(expectedEvent.getPath(), saved.getPath());
        assertEquals(expectedEvent.getIp(), saved.getIp());
        assertEquals(expectedEvent.getUserAgent(), saved.getUserAgent());
    }

    @Test
    void testSave_shouldThrowRuntimeExceptionWhenJsonIsInvalid() {
        String invalidJson = "{ invalid json }";

        assertThrows(RuntimeException.class, () -> pageViewInfoService.save(invalidJson));
        verify(pageViewInfoRepository, never()).save(any());
    }

    @Test
    void testGetPageViewStats_shouldAggregateStatsCorrectly() {
        LocalDateTime time1 = LocalDateTime.of(2025, 6, 15, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2025, 6, 16, 11, 30);

        PageViewEvent event1 = new PageViewEvent(null, time1, "session1", "/home", "192.168.0.1", "agent1");
        PageViewEvent event2 = new PageViewEvent(null, time1, "session2", "/about", "192.168.0.2", "agent2");
        PageViewEvent event3 = new PageViewEvent(null, time1, "session1", "/home", "192.168.0.3", "agent3"); // same session as event1
        PageViewEvent event4 = new PageViewEvent(null, time2, "session3", "/contact", "192.168.0.4", "agent4");

        when(pageViewInfoRepository.findAll()).thenReturn(List.of(event1, event2, event3, event4));

        LocalDate from = LocalDate.of(2025, 6, 14);
        LocalDate to = LocalDate.of(2025, 6, 17);

        List<PageViewStat> stats = pageViewInfoService.getPageViewStats(from, to);

        verify(pageViewInfoRepository, times(1)).findAll();
        assertThat(stats).hasSize(2);

        PageViewStat stat1 = stats.get(0);
        assertEquals("2025-06-15", stat1.visitTime());
        assertEquals(3, stat1.totalViews());
        assertEquals(2, stat1.uniqueVisitors());

        PageViewStat stat2 = stats.get(1);
        assertEquals("2025-06-16", stat2.visitTime());
        assertEquals(1, stat2.totalViews());
        assertEquals(1, stat2.uniqueVisitors());
    }

    @Test
    void testGetPageViewStats_shouldReturnEmptyListWhenNoEventsInRange() {
        PageViewEvent event = new PageViewEvent(null, LocalDateTime.of(2023, 1, 1, 12, 0), "sessionX", "/home", "127.0.0.1", "agentX");
        when(pageViewInfoRepository.findAll()).thenReturn(List.of(event));

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);

        List<PageViewStat> stats = pageViewInfoService.getPageViewStats(from, to);

        verify(pageViewInfoRepository, times(1)).findAll();
        assertThat(stats).isEmpty();
    }
}
