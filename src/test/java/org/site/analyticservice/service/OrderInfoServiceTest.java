package org.site.analyticservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.analyticservice.entity.OrderInfo;
import org.site.analyticservice.entity.OrderStat;
import org.site.analyticservice.repositiry.OrderInfoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderInfoServiceTest {

    @Mock
    private OrderInfoRepository orderInfoRepository;

    @InjectMocks
    private OrderInfoService orderInfoService;

    @Test
    void testSave_shouldDeserializeJsonAndSaveOrderInfo() {
        String json = """
                {
                  "orderId": "b9d6aef4-2c02-43fc-8d14-dc08737b8d91",
                  "bucketTime": [2024, 5, 30, 12, 0, 0, 0],
                  "totalItemsCount": 3,
                  "totalItemsAmount": 99.99
                }
                """;

        OrderInfo expectedOrderInfo = new OrderInfo();
        expectedOrderInfo.setOrderId(UUID.fromString("b9d6aef4-2c02-43fc-8d14-dc08737b8d91"));
        expectedOrderInfo.setBucketTime(LocalDateTime.of(2024, 5, 30, 12, 0));
        expectedOrderInfo.setTotalItemsCount(3);
        expectedOrderInfo.setTotalItemsAmount(99.99);

        when(orderInfoRepository.save(any(OrderInfo.class))).thenReturn(expectedOrderInfo);

        OrderInfo saved = orderInfoService.save(json);

        verify(orderInfoRepository, times(1)).save(any(OrderInfo.class));
        assertEquals(expectedOrderInfo.getOrderId(), saved.getOrderId());
        assertEquals(expectedOrderInfo.getBucketTime(), saved.getBucketTime());
        assertEquals(expectedOrderInfo.getTotalItemsCount(), saved.getTotalItemsCount());
        assertEquals(expectedOrderInfo.getTotalItemsAmount(), saved.getTotalItemsAmount());
    }

    @Test
    void testSave_shouldThrowRuntimeExceptionWhenJsonIsInvalid() {
        String invalidJson = "{ invalid json }";


        assertThrows(RuntimeException.class, () -> orderInfoService.save(invalidJson));
        verify(orderInfoRepository, never()).save(any());
    }

    @Test
    void testGetOrderStats_shouldAggregateStatsCorrectly() {
        LocalDateTime time1 = LocalDateTime.of(2024, 6, 10, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 6, 11, 11, 30);

        OrderInfo order1 = new OrderInfo(null, UUID.randomUUID(), time1, 2, 50.0);
        OrderInfo order2 = new OrderInfo(null, UUID.randomUUID(), time1, 1, 30.0);
        OrderInfo order3 = new OrderInfo(null, UUID.randomUUID(), time2, 3, 70.0);

        when(orderInfoRepository.findAll()).thenReturn(List.of(order1, order2, order3));

        LocalDate from = LocalDate.of(2024, 6, 9);
        LocalDate to = LocalDate.of(2024, 6, 12);

        List<OrderStat> stats = orderInfoService.getOrderStats(from, to);

        verify(orderInfoRepository, times(1)).findAll();
        assertThat(stats).hasSize(2);

        OrderStat stat1 = stats.get(0);
        assertEquals("2024-06-10", stat1.bucketTime());
        assertEquals(2, stat1.ordersCount());
        assertEquals(3, stat1.totalItemsCount());
        assertEquals(80.0, stat1.totalAmount());

        OrderStat stat2 = stats.get(1);
        assertEquals("2024-06-11", stat2.bucketTime());
        assertEquals(1, stat2.ordersCount());
        assertEquals(3, stat2.totalItemsCount());
        assertEquals(70.0, stat2.totalAmount());
    }

    @Test
    void testGetOrderStats_shouldReturnEmptyListWhenNoOrdersInRange() {
        OrderInfo order = new OrderInfo(null, UUID.randomUUID(), LocalDateTime.of(2023, 1, 1, 12, 0), 1, 10.0);
        when(orderInfoRepository.findAll()).thenReturn(List.of(order));

        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        List<OrderStat> stats = orderInfoService.getOrderStats(from, to);

        verify(orderInfoRepository, times(1)).findAll();
        assertThat(stats).isEmpty();
    }
}
