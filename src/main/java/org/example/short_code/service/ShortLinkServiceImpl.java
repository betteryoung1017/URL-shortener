package org.example.short_code.service;

import org.example.short_code.component.BloomFilterManager;
import org.example.short_code.mapper.ShortLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.example.short_code.pojo.ShortLink;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
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
    public static final char[] BASE62_DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    @Autowired
    private BloomFilterManager bloomFilterManager;
    private String getRedisKey(String shortCode){
        return REDIS_KEY_PREFIX + shortCode;
    }
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
    @Override
    @Transactional(rollbackFor = Exception.class)//事务管理，防止更新失败而导致短链为null的情况产生
    public String createShortLink(String url) {
        if(url == null || url.isEmpty())
            throw new IllegalArgumentException("URL不能为空");
        if(!url.startsWith("http://") && !url.startsWith("https://"))
            throw new IllegalArgumentException("URL必须以http://或https://开头");
        ShortLink shortLink = new ShortLink();
        shortLink.setUrl(url);
        shortLink.setShortCode(null);
        shortLink.setCreatedAt(java.time.LocalDateTime.now());
        shortLinkMapper.insert(shortLink);
        String shortCode = switchToBase62(shortLink.getId());
        shortLink.setShortCode(shortCode);
        try{
            stringRedisTemplate.delete(getRedisKey(shortCode));//防止短时间内有人访问过这个短链而导致Redis缓存残留
            shortLinkMapper.update(shortLink);
        }catch(DuplicateKeyException e){
            throw new IllegalArgumentException("短链已存在");//抛出异常，防止短链重复创建
        }
        bloomFilterManager.add(shortCode);
        return "http://localhost:8080/" + shortCode;
    }
    @Override
    public String getLongUrl(String shortCode) {
        if(!bloomFilterManager.contains(shortCode)){
            throw new IllegalArgumentException("短链不存在");
        }
        String key = getRedisKey(shortCode);
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            if(cached.isEmpty()){
                throw new IllegalArgumentException("短链不存在");
            }
            return cached;
        }
        ShortLink shortLink = shortLinkMapper.findByShortCode(shortCode);
        if(shortLink == null){
            stringRedisTemplate.opsForValue().set(key, "", NULL_TTL_MINUTES, TimeUnit.MINUTES); // 设置缓存为空字符串，防止缓存穿透
            throw new IllegalArgumentException("短链不存在");
        }
        String longUrl = shortLink.getUrl();
        stringRedisTemplate.opsForValue().set(key, longUrl, REAL_TTL_HOURS, TimeUnit.HOURS);
        return longUrl;
    }
}
