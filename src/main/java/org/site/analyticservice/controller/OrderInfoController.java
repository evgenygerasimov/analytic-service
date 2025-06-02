package org.site.analyticservice.controller;

import lombok.AllArgsConstructor;
import org.site.analyticservice.entity.OrderStat;
import org.site.analyticservice.service.OrderInfoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
public class OrderInfoController {

    private final OrderInfoService orderInfoService;

    @GetMapping("/order-info-dashboard")
    public String getOrderInfoDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        LocalDate endDate = (to != null) ? to : LocalDate.now();
        LocalDate startDate = (from != null) ? from : endDate.minusDays(6);

        List<OrderStat> stats = orderInfoService.getOrderStats(startDate, endDate);
        model.addAttribute("from", startDate);
        model.addAttribute("to", endDate);
        model.addAttribute("labels", stats.stream().map(OrderStat::bucketTime).toList());
        model.addAttribute("ordersData", stats.stream().map(OrderStat::ordersCount).toList());
        model.addAttribute("itemsData", stats.stream().map(OrderStat::totalItemsCount).toList());
        model.addAttribute(("amountsData"), stats.stream().map(OrderStat::totalAmount).toList());
        return "order-info-dashboard";
    }
}
