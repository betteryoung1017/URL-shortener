package org.example.short_code.mapper;

import org.apache.ibatis.annotations.*;
import org.example.short_code.pojo.ShortLink;
@Mapper
public interface ShortLinkMapper {
    @Insert("INSERT INTO shortlink (url, create_time) VALUES (#{url}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ShortLink shortLink);
    @Update("UPDATE shortlink SET shortcode = #{shortCode} where id = #{id}")
    int update(ShortLink shortLink);
    @Select("SELECT id, shortcode AS shortCode, url, create_time AS createdAt FROM shortlink WHERE shortcode = #{shortCode}")
    ShortLink findByShortCode(String shortCode);
}
