package org.example.short_code.service;

import org.example.short_code.pojo.ShortLinkResp;

public interface ShortLinkService {
    ShortLinkResp createShortLink(String url);
    String getLongUrl(String shortCode);
}
