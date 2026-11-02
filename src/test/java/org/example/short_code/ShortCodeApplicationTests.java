package org.example.short_code;

import mapper.ShortLinkMapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pojo.ShortLink;

@SpringBootTest
class ShortCodeApplicationTests {
    @Autowired
    private ShortLinkMapper shortLinkMapper;
    @Test
    void test(){
        ShortLink shortLink = new ShortLink();
        shortLink.setUrl("https://www.google.com");
        shortLink.setShortcode("google");
        int rows = shortLinkMapper.insert(shortLink);
        System.out.println("插入成功" + rows);
        ShortLink result = shortLinkMapper.findByShortCode("google");
        System.out.println("查询结果" + result);
    }

}
