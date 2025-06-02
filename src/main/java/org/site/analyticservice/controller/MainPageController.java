package org.site.analyticservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/")
public class MainPageController {

    @GetMapping
    public String index(@RequestHeader Map<String, String> headers) {
        return "index";
    }
}
