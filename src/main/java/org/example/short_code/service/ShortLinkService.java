package org.example.short_code.service;

public interface ShortLinkService {
    String createShortLink(String url);
    String getLongUrl(String shortCode);
}
