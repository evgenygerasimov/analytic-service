package org.site.analyticservice.controller;

import lombok.AllArgsConstructor;
import org.site.analyticservice.entity.PageViewStat;
import org.site.analyticservice.service.PageViewInfoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@AllArgsConstructor
public class PageViewInfoController {

    private final PageViewInfoService pageViewInfoService;

    @GetMapping("/page-view-dashboard")
    public String getPageViewDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        LocalDate endDate = (to != null) ? to : LocalDate.now();
        LocalDate startDate = (from != null) ? from : endDate.minusDays(6);

        List<PageViewStat> stats = pageViewInfoService.getPageViewStats(startDate, endDate);
        model.addAttribute("from", startDate);
        model.addAttribute("to", endDate);
        model.addAttribute("uniqueData", stats.stream().map(PageViewStat::uniqueVisitors).toList());
        model.addAttribute("labels", stats.stream().map(PageViewStat::visitTime).toList());
        model.addAttribute("viewsData", stats.stream().map(PageViewStat::totalViews).toList());

        return "page-view-dashboard";
    }
}
