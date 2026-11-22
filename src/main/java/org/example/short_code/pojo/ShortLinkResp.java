package org.example.short_code.pojo;

public class ShortLinkResp {
    private String shortUrl;
    private String summary;

    public ShortLinkResp(String shortUrl, String summary) {
        this.shortUrl = shortUrl;
        this.summary = summary;
    }

    public ShortLinkResp() {
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}