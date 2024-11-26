package com.ssafy.homesage.domain.ai.mapper;

import com.ssafy.homesage.domain.ai.model.dto.AnalyzeInfoResponseDto;
import com.ssafy.homesage.domain.ai.model.entity.Analyze;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnalyzeMapper {

    /**
     * 이메일로 해당하는 유저에 해당하는 분석방 조회
     */
    @Select("""
        SELECT analyze_id,
               analyze_name,
               created_at,
               updated_at
        FROM analyzes
        WHERE user_id = (
            SELECT user_id
            FROM users
            WHERE email = #{userEmail}
        )
    """)
    List<Analyze> getAnalyzeList(String userEmail);

    /**
     * 이메일로 새로운 분석 방 생성
     */
    @Insert("""
        INSERT INTO analyzes (user_id, analyze_name, registered_img_url, ledger_img_url, registered_result_text, ledger_result_text)
        VALUES
        (   
            (
                SELECT user_id
                FROM users
                WHERE email = #{userEmail}
            ),
            #{analyzeName},
            '',
            '',
            '',
            ''
        )
    """)
    void createAnalyze(String userEmail, String analyzeName);

    /**
     * 마지막으로 삽입된 요소의 id 반환
     */
    @Select("""
        SELECT LAST_INSERT_ID()
    """)
    int getLastInsertedId();

    /**
     * 분석방의 id에 해당하는 채팅방이 있는지의 여부 반환
     */
    @Select("""
        SELECT COUNT(analyze_id)
        FROM analyzes
        WHERE analyze_id = #{analyzeId}
    """)
    boolean isExistAnalyze(int analyzeId);

    /**
     * 분석방의 id에 해당하는 유저의 email 반환
     */
    @Select("""
        SELECT email
        FROM users
        WHERE user_id = (
            SELECT user_id
            FROM analyzes
            WHERE analyze_id = #{analyzeId}
        )
    """)
    String getAnalyzeOwnerEmail(int analyzeId);

    /**
     * 분석방의 id에 해당하는 분석 Info 반환
     */
    @Select("""
        SELECT registered_img_url, ledger_img_url, registered_result_text, ledger_result_text, registered_summary_text, ledger_summary_text, registered_score, ledger_score
        FROM analyzes
        WHERE analyze_id = #{analyzeId};
    """)
    AnalyzeInfoResponseDto getAnalyzeInfo(int analyzeId);

    @Delete("""
        DELETE 
        FROM analyzes
        WHERE analyze_id = #{analyzeId}
    """)
    void deleteAnalyze(int analyzeId);

    @Update("""
        UPDATE analyzes
        SET registered_img_url = #{url}
        WHERE analyze_id = #{analyzedId};
    """)
    void saveRegisteredUrl(int analyzedId, String url);

    @Update("""
        UPDATE analyzes
        SET ledger_img_url = #{url}
        WHERE analyze_id = #{analyzedId};
    """)
    void saveLedgerUrl(int analyzedId, String url);


    @Select("""
        SELECT registered_img_url
        FROM analyzes
        WHERE analyze_id = #{analyzedId};
    """)
    String getRegisteredUrl(int analyzedId);

    @Select("""
        SELECT ledger_img_url
        FROM analyzes
        WHERE analyze_id = #{analyzedId};
    """)
    String getLedgerUrl(int analyzedId);

    @Update("""
        UPDATE analyzes
        SET registered_result_text = #{result}, registered_summary_text = #{summary}, registered_score = #{score}
        WHERE analyze_id = #{analyzedId};
    """)
    void insertRegisteredAnalyzeResult(String result, String summary, String score, int analyzedId);

    @Update("""
        UPDATE analyzes
        SET ledger_result_text = #{result}, ledger_summary_text = #{summary}, ledger_score = #{score}
        WHERE analyze_id = #{analyzedId};
    """)
    void insertLedgerAnalyzeResult(String result, String summary, String score, int analyzedId);

}
