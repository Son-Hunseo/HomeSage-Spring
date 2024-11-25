package com.ssafy.homesage.domain.info.mapper;

import com.ssafy.homesage.domain.info.model.entity.Info;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InfoMapper {

    /**
     * 해당 페이지에 해당하는
     */
    @Select("""
        SELECT info_id,
               title,
               content,
               views,
               created_at,
               updated_at
        FROM info
        ORDER BY created_at DESC
        LIMIT 7 OFFSET ${offset}
    """)
    List<Info> getTargetPageInfos(int offset);

    @Update("""
        UPDATE info
        SET views = views + 1
        WHERE info_id = #{infoNum}
    """)
    void increaseViews(int infoNum);

    @Select("""
        SELECT FLOOR(COUNT(*)/7) + 1
        FROM info
    """)
    int getTotalNumOfPage();
}
