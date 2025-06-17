package org.site.analyticservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.AllArgsConstructor;
import org.site.analyticservice.entity.OrderInfo;
import org.site.analyticservice.entity.OrderStat;
import org.site.analyticservice.mapper.LocalDateTimeArrayDeserializer;
import org.site.analyticservice.repositiry.OrderInfoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderInfoService {

    private final OrderInfoRepository orderInfoRepository;

    public OrderInfo save(String jsonOrderInfo) {
        OrderInfo orderInfo;
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeArrayDeserializer());

        objectMapper.registerModule(module);
        try {
            orderInfo = objectMapper.readValue(jsonOrderInfo, OrderInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return orderInfoRepository.save(orderInfo);
    }

    public List<OrderStat> getOrderStats(LocalDate from, LocalDate to) {
        List<OrderInfo> allOrders = orderInfoRepository.findAll();

        return allOrders.stream()
                .filter(order -> {
                    LocalDate date = order.getBucketTime().toLocalDate();
                    return (date.compareTo(from) >= 0 && date.compareTo(to) <= 0);
                })
                .collect(Collectors.groupingBy(
                        order -> order.getBucketTime().toLocalDate().toString()
                ))
                .entrySet().stream()
                .map(entry -> {
                    String date = entry.getKey();
                    List<OrderInfo> orders = entry.getValue();
                    int ordersCount = orders.size();
                    int totalItems = orders.stream()
                            .mapToInt(OrderInfo::getTotalItemsCount)
                            .sum();
                    double totalAmount = orders.stream()
                            .mapToDouble(OrderInfo::getTotalItemsAmount)
                            .sum();
                    return new OrderStat(date, ordersCount, totalItems, totalAmount);
                })
                .sorted(Comparator.comparing(OrderStat::bucketTime))
                .toList();
    }
}
