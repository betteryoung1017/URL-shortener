package org.example.short_code.service;

import org.example.short_code.mapper.ShortLinkMapper;
import org.example.short_code.pojo.ShortLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ShortLinkWriteService {
    @Autowired
    private ShortLinkMapper shortLinkMapper;
    public static final char[] BASE62_DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    //利用自增id进行62进制转换生成短链
    private String switchToBase62(long id) {
        long new_number = id * 1000000L;  //乘1000000L防止爬虫遍历真实数据量
        String shortCode = "";
        while (new_number > 0) {
            int digit = (int) (new_number % 62);
            shortCode = BASE62_DIGITS[digit] + shortCode;
            new_number /= 62;
        }
        return shortCode;
    }
    @Transactional(rollbackFor = Exception.class)//事务管理，防止更新失败而导致短链为null的情况产生
    public ShortLink conserveShortLink(String url) {
        ShortLink shortLink = new ShortLink();
        shortLink.setUrl(url);
        shortLink.setShortCode(null);
        shortLink.setCreatedAt(java.time.LocalDateTime.now());
        shortLink.setSummary(null);
        shortLinkMapper.insert(shortLink);
        String shortCode = switchToBase62(shortLink.getId());
        shortLink.setShortCode(shortCode);
        shortLinkMapper.update(shortLink);
        return shortLink;
    }
}
