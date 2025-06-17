package org.site.analyticservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.analyticservice.entity.PageViewStat;
import org.site.analyticservice.service.PageViewInfoService;
import org.site.analyticservice.utils.JwtParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PageViewInfoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PageViewInfoService pageViewInfoService;

    @Mock
    private JwtParser jwtParser;

    @InjectMocks
    private PageViewInfoController pageViewInfoController;

    @BeforeEach
    void setUp() {
        var viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders
                .standaloneSetup(pageViewInfoController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void testGetPageViewDashboard_withDefaultDates() throws Exception {
        String accessToken = "mock-token";
        UUID userId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        LocalDate defaultFrom = now.minusDays(6);

        List<PageViewStat> mockStats = List.of(
                new PageViewStat("2024-06-10", 100, 80),
                new PageViewStat("2024-06-11", 150, 120)
        );

        when(jwtParser.accessTokenExtractor(anyMap())).thenReturn(accessToken);
        when(jwtParser.extractUserId(accessToken)).thenReturn(userId.toString());
        when(pageViewInfoService.getPageViewStats(defaultFrom, now)).thenReturn(mockStats);

        mockMvc.perform(get("/page-view-dashboard")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(view().name("page-view-dashboard"))
                .andExpect(model().attribute("from", defaultFrom))
                .andExpect(model().attribute("to", now))
                .andExpect(model().attributeExists("labels"))
                .andExpect(model().attributeExists("viewsData"))
                .andExpect(model().attributeExists("uniqueData"))
                .andExpect(model().attribute("authUserId", userId.toString()));
    }

    @Test
    void testGetPageViewDashboard_withCustomDates() throws Exception {
        String accessToken = "mock-token";
        UUID userId = UUID.randomUUID();
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 3);

        List<PageViewStat> mockStats = List.of(
                new PageViewStat("2024-01-01", 200, 150),
                new PageViewStat("2024-01-03", 100, 70)
        );

        when(jwtParser.accessTokenExtractor(anyMap())).thenReturn(accessToken);
        when(jwtParser.extractUserId(accessToken)).thenReturn(userId.toString());
        when(pageViewInfoService.getPageViewStats(from, to)).thenReturn(mockStats);

        mockMvc.perform(get("/page-view-dashboard")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-03"))
                .andExpect(status().isOk())
                .andExpect(view().name("page-view-dashboard"))
                .andExpect(model().attribute("from", from))
                .andExpect(model().attribute("to", to))
                .andExpect(model().attributeExists("labels"))
                .andExpect(model().attributeExists("viewsData"))
                .andExpect(model().attributeExists("uniqueData"))
                .andExpect(model().attribute("authUserId", userId.toString()));
    }
}
