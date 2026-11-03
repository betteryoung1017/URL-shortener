package org.example.short_code.pojo;

import java.time.LocalDateTime;

public class ShortLink {
    private Long id;
    private String shortCode;
    private String url;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortcode() {
        return shortCode;
    }

    public void setShortcode(String shortcode) {
        this.shortCode = shortcode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
