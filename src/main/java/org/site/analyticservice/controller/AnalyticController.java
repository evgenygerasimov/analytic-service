package org.site.analyticservice.controller;

import lombok.AllArgsConstructor;
import org.site.analyticservice.entity.OrderStat;
import org.site.analyticservice.service.OrderInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller("/")
@AllArgsConstructor
public class AnalyticController {

    private final OrderInfoService orderInfoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {


        List<OrderStat> stats = orderInfoService.getOrderStats();
        model.addAttribute("labels", stats.stream().map(OrderStat::bucketTime).toList());
        model.addAttribute("ordersData", stats.stream().map(OrderStat::ordersCount).toList());
        model.addAttribute("itemsData", stats.stream().map(OrderStat::totalItemsCount).toList());
        model.addAttribute(("amountsData"), stats.stream().map(OrderStat::totalAmount).toList());
        return "dashboard";
    }
}
