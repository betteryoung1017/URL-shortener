package org.example.short_code.service;

import org.example.short_code.mapper.ShortLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.example.short_code.pojo.ShortLink;
import org.springframework.dao.DuplicateKeyException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {
    @Autowired
    private ShortLinkMapper shortLinkMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String REDIS_KEY_PREFIX = "shortlink:";
    private static final long REAL_TTL_HOURS = 24;
    private static final long NULL_TTL_MINUTES = 5;
    private String getRedisKey(String shortCode){
        return REDIS_KEY_PREFIX + shortCode;
    }
    @Override
    public String createShortLink(String url) {
        if(url == null || url.isEmpty())
            throw new IllegalArgumentException("URL不能为空");
        if(!url.startsWith("http://") && !url.startsWith("https://"))
            throw new IllegalArgumentException("URL必须以http://或https://开头");
        String shortCode = UUID.randomUUID().toString().replace("-","").substring(0, 10);
        ShortLink shortLink = new ShortLink();
        shortLink.setUrl(url);
        shortLink.setShortcode(shortCode);
        shortLink.setCreatedAt(java.time.LocalDateTime.now());
        try{
            shortLinkMapper.insert(shortLink);
        }catch(DuplicateKeyException e){
            throw new RuntimeException("该短链已存在，请重新生成",e);
        }
        return "http://localhost:8080/" + shortCode;
    }
    public String getLongUrl(String shortCode) {
        String key = getRedisKey(shortCode);
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached.isEmpty() ? null : cached;
        }
        ShortLink shortLink = shortLinkMapper.findByShortCode(shortCode);
        if(shortLink == null){
            throw new IllegalArgumentException("短链不存在");
        }
        String longUrl = shortLink.getUrl();
        if (longUrl != null) {
            stringRedisTemplate.opsForValue().set(key, longUrl, REAL_TTL_HOURS, TimeUnit.HOURS);
        } else {
            stringRedisTemplate.opsForValue().set(key, "", NULL_TTL_MINUTES, TimeUnit.MINUTES);
        }

        return shortLink.getUrl();
    }
}
