package org.site.analyticservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.AllArgsConstructor;
import org.site.analyticservice.entity.PageViewEvent;
import org.site.analyticservice.entity.PageViewStat;
import org.site.analyticservice.mapper.LocalDateTimeArrayDeserializer;
import org.site.analyticservice.repositiry.PageViewInfoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PageViewInfoService {

    private final PageViewInfoRepository pageViewInfoRepository;

    public PageViewEvent save(String pageViewEventJson) {
        PageViewEvent pageViewEvent;
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeArrayDeserializer());

        objectMapper.registerModule(module);

        try {
            pageViewEvent = objectMapper.readValue(pageViewEventJson, PageViewEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return pageViewInfoRepository.save(pageViewEvent);
    }

    public List<PageViewStat> getPageViewStats(LocalDate from, LocalDate to) {
        List<PageViewEvent> allEvents = pageViewInfoRepository.findAll();

        return allEvents.stream()
                .filter(event -> {
                    LocalDate date = event.getVisitTime().toLocalDate();
                    return !date.isBefore(from) && !date.isAfter(to);
                })
                .collect(Collectors.groupingBy(event -> {
                    LocalDateTime time = event.getVisitTime();
                    return time.toLocalDate().toString();
                }))
                .entrySet().stream()
                .map(entry -> {
                    String visitTime = entry.getKey();
                    List<PageViewEvent> events = entry.getValue();

                    int viewsCount = events.size();
                    long uniqueVisitors = events.stream()
                            .map(PageViewEvent::getSessionId)
                            .filter(Objects::nonNull)
                            .distinct()
                            .count();

                    return new PageViewStat(visitTime, viewsCount, (int) uniqueVisitors);
                })
                .sorted(Comparator.comparing(PageViewStat::visitTime))
                .toList();
    }
}
