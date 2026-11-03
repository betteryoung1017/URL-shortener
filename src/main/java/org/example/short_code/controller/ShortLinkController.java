package org.example.short_code.controller;

import org.example.short_code.service.ShortLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShortLinkController {
    @Autowired
    private ShortLinkService shortLinkService;
    @GetMapping("/create")
    public String creatShortLink(@RequestParam String url){
        return shortLinkService.createShortLink(url);
    }
}
