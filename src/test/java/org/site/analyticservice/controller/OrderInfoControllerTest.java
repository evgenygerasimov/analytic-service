package org.site.analyticservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.analyticservice.entity.OrderStat;
import org.site.analyticservice.service.OrderInfoService;
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
class OrderInfoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderInfoService orderInfoService;

    @Mock
    private JwtParser jwtParser;

    @InjectMocks
    private OrderInfoController orderInfoController;

    @BeforeEach
    void setUp() {
        var viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderInfoController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void testGetOrderInfoDashboard_withDefaultDates() throws Exception {
        String accessToken = "mock-token";
        UUID userId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        LocalDate defaultFrom = now.minusDays(6);

        List<OrderStat> mockStats = List.of(
                new OrderStat("2024-06-10", 3, 7, 100.0),
                new OrderStat("2024-06-11", 5, 10, 200.0)
        );

        when(jwtParser.accessTokenExtractor(anyMap())).thenReturn(accessToken);
        when(jwtParser.extractUserId(accessToken)).thenReturn(userId.toString());
        when(orderInfoService.getOrderStats(defaultFrom, now)).thenReturn(mockStats);

        mockMvc.perform(get("/order-info-dashboard")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(view().name("order-info-dashboard"))
                .andExpect(model().attribute("from", defaultFrom))
                .andExpect(model().attribute("to", now))
                .andExpect(model().attributeExists("labels"))
                .andExpect(model().attributeExists("ordersData"))
                .andExpect(model().attributeExists("itemsData"))
                .andExpect(model().attributeExists("amountsData"))
                .andExpect(model().attribute("authUserId", userId.toString()));
    }

    @Test
    void testGetOrderInfoDashboard_withCustomDates() throws Exception {
        String accessToken = "mock-token";
        UUID userId = UUID.randomUUID();
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 3);

        List<OrderStat> mockStats = List.of(
                new OrderStat("2024-01-01", 2, 4, 50.0),
                new OrderStat("2024-01-03", 1, 2, 30.0)
        );

        when(jwtParser.accessTokenExtractor(anyMap())).thenReturn(accessToken);
        when(jwtParser.extractUserId(accessToken)).thenReturn(userId.toString());
        when(orderInfoService.getOrderStats(from, to)).thenReturn(mockStats);

        mockMvc.perform(get("/order-info-dashboard")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-03"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-info-dashboard"))
                .andExpect(model().attribute("from", from))
                .andExpect(model().attribute("to", to))
                .andExpect(model().attributeExists("labels"))
                .andExpect(model().attributeExists("ordersData"))
                .andExpect(model().attributeExists("itemsData"))
                .andExpect(model().attributeExists("amountsData"))
                .andExpect(model().attribute("authUserId", userId.toString()));
    }
}
