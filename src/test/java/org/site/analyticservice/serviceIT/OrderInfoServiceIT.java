package org.site.analyticservice.serviceIT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.site.analyticservice.TestContainerConfig;
import org.site.analyticservice.entity.OrderInfo;
import org.site.analyticservice.entity.OrderStat;
import org.site.analyticservice.repositiry.OrderInfoRepository;
import org.site.analyticservice.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OrderInfoServiceIT extends TestContainerConfig {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @BeforeEach
    void clearDb() {
        orderInfoRepository.deleteAll();
    }

    @Test
    void testSaveOrderInfo() {

        String json = """
                {
                  "orderId": "b9d6aef4-2c02-43fc-8d14-dc08737b8d91",
                  "bucketTime": [2024, 5, 30, 12, 0, 0, 0],
                  "totalItemsCount": 3,
                  "totalItemsAmount": 150.75
                }
                """;

        OrderInfo saved = orderInfoService.save(json);

        assertThat(saved.getOrderId()).isNotNull();
        assertThat(saved.getTotalItemsCount()).isEqualTo(3);
        assertThat(saved.getTotalItemsAmount()).isEqualTo(150.75);
        assertThat(saved.getBucketTime()).isEqualTo(LocalDateTime.of(2024, 5, 30, 12, 0, 0, 0));
    }

    @Test
    void testGetOrderStatsBetweenDates() {
        OrderInfo order1 = OrderInfo.builder()
                .orderId(UUID.randomUUID())
                .bucketTime(LocalDateTime.of(2025, 6, 15, 10, 0))
                .totalItemsCount(2)
                .totalItemsAmount(100.0)
                .build();

        OrderInfo order2 = OrderInfo.builder()
                .orderId(UUID.randomUUID())
                .bucketTime(LocalDateTime.of(2025, 6, 16, 11, 0))
                .totalItemsCount(1)
                .totalItemsAmount(50.0)
                .build();

        OrderInfo order3 = OrderInfo.builder()
                .orderId(UUID.randomUUID())
                .bucketTime(LocalDateTime.of(2025, 6, 16, 12, 0))
                .totalItemsCount(4)
                .totalItemsAmount(200.0)
                .build();

        orderInfoRepository.save(order1);
        orderInfoRepository.save(order2);
        orderInfoRepository.save(order3);

        LocalDate from = LocalDate.of(2025, 6, 15);
        LocalDate to = LocalDate.of(2025, 6, 16);

        List<OrderStat> stats = orderInfoService.getOrderStats(from, to);

        assertThat(stats).hasSize(2);

        assertThat(stats.get(0).bucketTime()).isEqualTo("2025-06-15");
        assertThat(stats.get(0).ordersCount()).isEqualTo(1);
        assertThat(stats.get(0).totalItemsCount()).isEqualTo(2);
        assertThat(stats.get(0).totalAmount()).isEqualTo(100.0);

        assertThat(stats.get(1).bucketTime()).isEqualTo("2025-06-16");
        assertThat(stats.get(1).ordersCount()).isEqualTo(2);
        assertThat(stats.get(1).totalItemsCount()).isEqualTo(5);
        assertThat(stats.get(1).totalAmount()).isEqualTo(250.0);
    }
}
