package org.example.short_code.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.short_code.pojo.ShortLink;
@Mapper
public interface ShortLinkMapper {
    @Insert("INSERT INTO shortlink (shortcode, url, create_time) VALUES (#{shortcode}, #{url}, #{createdAt})")
    int insert(ShortLink shortLink);
    @Select("SELECT id, shortcode AS shortCode, url, create_time AS createdAt FROM shortlink WHERE shortcode = #{shortCode}")
    ShortLink findByShortCode(String shortCode);
}
