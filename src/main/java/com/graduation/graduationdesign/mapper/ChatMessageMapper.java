package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.ChatMessage;
import com.graduation.graduationdesign.vo.ChatMessageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 聊天消息Mapper
 */
@Mapper
public interface ChatMessageMapper {

    // 发送消息
    @Insert("insert into chat_message(session_id, sender_id, receiver_id, content, type, status, send_time, create_time) " +
            "values(#{sessionId}, #{senderId}, #{receiverId}, #{content}, #{type}, #{status}, now(), now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ChatMessage message);

    // 查询会话聊天记录
    @Select("select cm.id, cm.session_id, cm.sender_id, cm.receiver_id, cm.content, cm.type, cm.status, cm.send_time, " +
            "u1.username as sender_name, u1.avatar as sender_avatar, " +
            "u2.username as receiver_name, u2.avatar as receiver_avatar " +
            "from chat_message cm " +
            "left join user u1 on cm.sender_id = u1.id " +
            "left join user u2 on cm.receiver_id = u2.id " +
            "where cm.session_id = #{sessionId} " +
            "order by cm.send_time asc")
    List<ChatMessageVO> getChatHistory(String sessionId);

    @Update("update chat_message set status = 1 where session_id = #{sessionId} and receiver_id = #{userId} and status = 0")
    void markRead(String sessionId, Long userId);
}
