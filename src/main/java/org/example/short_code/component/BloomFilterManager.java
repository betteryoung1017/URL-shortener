package org.example.short_code.component;

import org.example.short_code.mapper.ShortLinkMapper;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BloomFilterManager implements CommandLineRunner {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ShortLinkMapper shortLinkMapper;
    private RBloomFilter<String> bloomFilter;
    @Override
    public void run(String... args) throws Exception {
        bloomFilter = redissonClient.getBloomFilter("short_link");
        bloomFilter.tryInit(1000000, 0.01);
        List<String> allcodes = shortLinkMapper.getAllCodes();
        if(allcodes != null && !allcodes.isEmpty()){
            for(String code : allcodes){
                bloomFilter.add(code);
            }
        }
    }
    public boolean contains(String shortCode){
        return bloomFilter.contains(shortCode);
    }
    public void add(String shortCode){
        bloomFilter.add(shortCode);
    }
}
