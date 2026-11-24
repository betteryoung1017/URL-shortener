package org.example.short_code;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.example.short_code.service.ShortLinkService;

/**
 * 压测数据生成工具。
 * <p>
 * 默认禁用：执行会向真实数据库批量写入数据，仅在做压测准备时手动启用。
 * 启用前请确认 application.yml 中 app.ai.enabled=false，否则每条记录都会调用两次 AI。
 */
@Disabled("压测数据生成工具，需要时手动移除 @Disabled 后运行")
@SpringBootTest
class DataGenTest {

    @Autowired
    private ShortLinkService shortLinkService;

    @Test
    void gen() {
        for (int i = 0; i < 1200; i++) {
            shortLinkService.createShortLink("https://example.com/page/" + i);
            if (i % 100 == 0) {
                System.out.println("已生成 " + i);
            }
        }
    }
}
