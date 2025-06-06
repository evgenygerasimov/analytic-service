package org.site.analyticservice.controller;

import lombok.AllArgsConstructor;
import org.site.analyticservice.entity.PageViewStat;
import org.site.analyticservice.service.PageViewInfoService;
import org.site.analyticservice.utils.JwtParser;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class PageViewInfoController {

    private final PageViewInfoService pageViewInfoService;
    private final JwtParser jwtParser;

    @GetMapping("/page-view-dashboard")
    public String getPageViewDashboard(@RequestHeader Map<String, String> headers,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                       Model model) {
        String accessToken = jwtParser.accessTokenExtractor(headers);

        LocalDate endDate = (to != null) ? to : LocalDate.now();
        LocalDate startDate = (from != null) ? from : endDate.minusDays(6);

        List<PageViewStat> stats = pageViewInfoService.getPageViewStats(startDate, endDate);
        model.addAttribute("from", startDate);
        model.addAttribute("to", endDate);
        model.addAttribute("uniqueData", stats.stream().map(PageViewStat::uniqueVisitors).toList());
        model.addAttribute("labels", stats.stream().map(PageViewStat::visitTime).toList());
        model.addAttribute("viewsData", stats.stream().map(PageViewStat::totalViews).toList());
        model.addAttribute("authUserId", jwtParser.extractUserId(accessToken));
        return "page-view-dashboard";
    }
}
