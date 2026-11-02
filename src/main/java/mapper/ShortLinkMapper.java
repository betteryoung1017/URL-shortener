package mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pojo.ShortLink;
@Mapper
public interface ShortLinkMapper {
    @Insert("INSERT INTO shortlink (shortcode, url, creat_time) VALUES (#{shortcode}, #{url}, #{createdAt})")
    int insert(ShortLink shortLink);
    @Select("SELECT id, shortcode AS shortCode, url, creat_time AS createdAt FROM shortlink WHERE shortcode = #{shortcode}")
    ShortLink findByShortCode(String shortCode);
}
