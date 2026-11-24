package org.example.short_code.service;

import org.example.short_code.component.AIComponent;
import org.example.short_code.component.BloomFilterManager;
import org.example.short_code.exception.ShortLinkNotFoundException;
import org.example.short_code.mapper.ShortLinkMapper;
import org.example.short_code.pojo.ShortLinkResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.example.short_code.pojo.ShortLink;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.TimeUnit;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {
    @Autowired
    private ShortLinkMapper shortLinkMapper;
    @Autowired
    private ShortLinkWriteService shortLinkWriteService;
    @Autowired
    private AIComponent aiComponent;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String REDIS_KEY_PREFIX = "shortlink:";
    private static final long REAL_TTL_HOURS = 24;
    private static final long NULL_TTL_MINUTES = 5;
    @Autowired
    private BloomFilterManager bloomFilterManager;
    @Value("${app.cache.mode}")
    private String cacheMode;
    @Value("${app.base-url}")
    private String webBase;
    private String getRedisKey(String shortCode){
        return REDIS_KEY_PREFIX + shortCode;
    }
    @Override
    public ShortLinkResp createShortLink(String url) {
        if(url == null || url.isEmpty())
            throw new IllegalArgumentException("URL不能为空");
        if(!url.startsWith("http://") && !url.startsWith("https://"))
            throw new IllegalArgumentException("URL必须以http://或https://开头");
        if(!aiComponent.isSafe(url)){
            throw new IllegalArgumentException("URL包含非法内容");
        }
        ShortLink shortLink;
        try{
            shortLink = shortLinkWriteService.conserveShortLink(url);
        }catch(DuplicateKeyException e){
            throw new IllegalArgumentException("短链已存在");//抛出异常，防止短链重复创建
        }
        String shortCode = shortLink.getShortCode();
        String summary = aiComponent.summarize(url);
        if(summary != null){
            shortLink.setSummary(summary);
            shortLinkMapper.updateSummary(summary, shortLink.getId());
        }
        stringRedisTemplate.delete(getRedisKey(shortCode));//防止短时间内有人访问过这个短链而导致Redis缓存残留
        bloomFilterManager.add(shortCode);
        ShortLinkResp shortLinkResp = new ShortLinkResp(webBase+"/"+shortCode, summary);
        return shortLinkResp;
    }
    @Override
    public String getLongUrl(String shortCode) {
        String longUrl;
        if("none".equals(cacheMode)){
            ShortLink shortLink = shortLinkMapper.findByShortCode(shortCode);
            if(shortLink == null){
                throw new ShortLinkNotFoundException("短链不存在");
            }
            return shortLink.getUrl();
        }
        else if("redis".equals(cacheMode)){
            String key = getRedisKey(shortCode);
            String cached = stringRedisTemplate.opsForValue().get(key);
            if (cached != null) {
                if(cached.isEmpty()){
                    throw new ShortLinkNotFoundException("短链不存在");
                }
                return cached;
            }
            ShortLink shortLink = shortLinkMapper.findByShortCode(shortCode);
            if(shortLink == null){
                stringRedisTemplate.opsForValue().set(key, "", NULL_TTL_MINUTES, TimeUnit.MINUTES); // 设置缓存为空字符串，防止缓存穿透
                throw new ShortLinkNotFoundException("短链不存在");
            }
            longUrl = shortLink.getUrl();
            stringRedisTemplate.opsForValue().set(key, longUrl, REAL_TTL_HOURS, TimeUnit.HOURS);
        }
        else {
            if (!bloomFilterManager.contains(shortCode)) {
                throw new ShortLinkNotFoundException("短链不存在");
            }
            String key = getRedisKey(shortCode);
            String cached = stringRedisTemplate.opsForValue().get(key);
            if (cached != null) {
                if (cached.isEmpty()) {
                    throw new ShortLinkNotFoundException("短链不存在");
                }
                return cached;
            }
            ShortLink shortLink = shortLinkMapper.findByShortCode(shortCode);
            if (shortLink == null) {
                stringRedisTemplate.opsForValue().set(key, "", NULL_TTL_MINUTES, TimeUnit.MINUTES); // 设置缓存为空字符串，防止缓存穿透
                throw new ShortLinkNotFoundException("短链不存在");
            }
            longUrl = shortLink.getUrl();
            stringRedisTemplate.opsForValue().set(key, longUrl, REAL_TTL_HOURS, TimeUnit.HOURS);
        }
        return longUrl;
    }
}
