package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.ChatSession;
import com.graduation.graduationdesign.vo.ChatSessionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 聊天会话Mapper
 */
@Mapper
public interface ChatSessionMapper {

    @Select("select * from chat_session where user_id = #{userId} and other_user_id = #{otherUserId} limit 1")
    ChatSession findByUserAndOther(Long userId, Long otherUserId);

    @Select("select * from chat_session where session_id = #{sessionId} and user_id = #{userId} limit 1")
    ChatSession findBySessionIdAndUser(String sessionId, Long userId);

    @Insert("insert into chat_session(session_id, user_id, other_user_id, last_message, last_time, unread_count, create_time, update_time) " +
            "values(#{sessionId}, #{userId}, #{otherUserId}, #{lastMessage}, #{lastTime}, #{unreadCount}, now(), now())")
    void insert(ChatSession session);

    @Update("update chat_session set last_message = #{lastMessage}, last_time = #{lastTime}, " +
            "unread_count = #{unreadCount}, update_time = now() where id = #{id}")
    void updateSession(ChatSession session);

    @Update("update chat_session set unread_count = 0, update_time = now() where session_id = #{sessionId} and user_id = #{userId}")
    void clearUnread(String sessionId, Long userId);

    @Select("select cs.id, cs.session_id, cs.user_id, cs.other_user_id, cs.last_message, cs.last_time, cs.unread_count, " +
            "u.username as other_name, u.avatar as other_avatar " +
            "from chat_session cs " +
            "left join user u on cs.other_user_id = u.id " +
            "where cs.user_id = #{userId} order by cs.last_time desc")
    List<ChatSessionVO> listSessions(Long userId);

    @Select("select cs.id, cs.session_id, cs.user_id, cs.other_user_id, cs.last_message, cs.last_time, cs.unread_count, " +
            "u.username as other_name, u.avatar as other_avatar " +
            "from chat_session cs " +
            "left join user u on cs.other_user_id = u.id " +
            "where cs.user_id = #{userId} and (" +
            "u.username like concat('%',#{keyword},'%') " +
            "or exists (select 1 from chat_message cm where cm.session_id = cs.session_id " +
            "and cm.content like concat('%',#{keyword},'%'))" +
            ") order by cs.last_time desc")
    List<ChatSessionVO> searchSessions(Long userId, String keyword);
}
