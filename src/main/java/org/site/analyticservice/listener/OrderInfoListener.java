package org.site.analyticservice.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.site.analyticservice.service.OrderInfoService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class OrderInfoListener {

    private final OrderInfoService orderInfoService;

    @KafkaListener(topics = "order.info", groupId = "notification-group")
    public void listenOrderCreated(String jsonOrderInfo) {
        orderInfoService.save(jsonOrderInfo);
    }
}