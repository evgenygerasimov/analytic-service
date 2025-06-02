package org.site.analyticservice.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.site.analyticservice.service.PageViewInfoService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PageViewListener {

    private final PageViewInfoService pageViewInfoService;

    @KafkaListener(topics = "page-view-info", groupId = "notification-group")
    public void listenOrderCreated(String pageViewEventJson) {
        log.info("Received page view event: {}", pageViewEventJson);
        pageViewInfoService.save(pageViewEventJson);
    }
}
