package org.example.short_code.service;

import org.example.short_code.mapper.ShortLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.short_code.pojo.ShortLink;
import org.springframework.dao.DuplicateKeyException;
import java.util.UUID;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {
    @Autowired
    private ShortLinkMapper shortLinkMapper;
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
    public String getLongUrl(String shortCode){
        ShortLink shortLink = shortLinkMapper.findByShortCode(shortCode);
        if(shortLink == null) {
            throw new IllegalArgumentException("短链不存在，请重新查询");
        }
        return shortLink.getUrl();
    }
}
