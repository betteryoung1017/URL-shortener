package org.example.short_code;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.example.short_code.service.ShortLinkService;


@SpringBootTest
class ShortCodeApplicationTests {
    @Autowired
    private ShortLinkService shortLinkService;
    @Test
    void test(){
        String shortCode = shortLinkService.createShortLink("https://www.baidu.com");
        System.out.println("生成的短链是"+shortCode);
    }

}
