package com.ssafy.homesage.domain.ai.mapper;

import java.util.List;

import com.ssafy.homesage.domain.ai.model.entity.AIMessage;
import com.ssafy.homesage.domain.ai.model.entity.ChatRoom;
import com.ssafy.homesage.domain.ai.model.entity.HumanMessage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChatMapper {

    /**
     * 이메일로 해당 유저에 해당하는 채팅방 조회
     */
    @Select("""
        SELECT chat_room_id,
               created_at,
               updated_at
        FROM chat_room
        WHERE user_id = (
            SELECT user_id
            FROM users
            WHERE email = #{userEmail}
    )
    """)
    List<ChatRoom> getChatList(String userEmail);

    /**
     * 이메일로 새로운 채팅방 생성하고,
     * 해당 세션의 마지막 insert된 id 가져옴 (해당 세션이기 때문에 다른 insert 안겹침)
     */
    @Insert("""
        INSERT INTO chat_room (user_id)
        SELECT user_id
        FROM users
        WHERE email = #{userEmail}
    """)
    void createChatRoom(String userEmail);

    /**
     * 마지막으로 삽입된 요소의 id 반환
     */
    @Select("""
        SELECT LAST_INSERT_ID()
    """)
    int getLastInsertedId();

    /**
     * 채팅방이 생겼을 때, seq가 0인 디폴트 메시지 필요
     */
    @Insert("""
        INSERT INTO chat_message (chat_room_id, type, message, message_seq)
        VALUES (#{chatRoomId}, "AI", "부동산에대한 전반적인 질의응답을 하는 챗봇입니다. 부동산 용어나, 계약시 유의해야할 점 등을 질문해주세요!", 0)
    """)
    void insertDefaultChatMessage(int chatRoomId);

    /**
     * 채팅방의 id에 해당하는 채팅방이 있는지의 여부 반환
     */
    @Select("""
        SELECT *
        FROM chat_room
        WHERE chat_room_id = #{chatRoomId}
    """)
    boolean isExistChatRoom(int chatRoomId);

    /**
     * 채팅방의 id에 해당하는 유저의 email 반환
     */
    @Select("""
        SELECT email
        FROM users
        WHERE user_id = (
            SELECT user_id
            FROM chat_room
            WHERE chat_room_id = #{chatRoomId}
        )
    """)
    String getChatRoomOwnerEmail(int chatRoomId);

    /**
     * 채팅방의 id에 해당하는 AI Message 반환
     */
    @Select("""
        SELECT type, message, message_seq
        FROM chat_message
        WHERE chat_room_id = #{chatRoomId} 
            AND type = "AI"
    """)
    List<AIMessage> getAIMessageList(int chatRoomId);

    /**
     * 채팅방의 id에 해당하는 Human Message 반환
     */
    @Select("""
        SELECT type, message, message_seq
        FROM chat_message
        WHERE chat_room_id = #{chatRoomId} 
            AND type = "HUMAN"
    """)
    List<HumanMessage> getHumanMessageList(int chatRoomId);

    @Delete("""
        DELETE 
        FROM chat_message
        WHERE chat_room_id = #{chatRoomId}
    """)
    void deleteChatMessageList(int chatRoomId);

    @Delete("""
        DELETE 
        FROM chat_room
        WHERE chat_room_id = #{chatRoomId}
    """)
    void deleteChatRoom(int chatRoomId);
}
