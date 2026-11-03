package org.example.short_code.controller;

import org.example.short_code.service.ShortLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
public class RedirectController {
    @Autowired
    private ShortLinkService shortLinkService;
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode)
    {
        String url = shortLinkService.getLongUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
    }
}
