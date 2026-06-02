package com.chat.was.chat.dao;

import com.chat.was.chat.vo.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 채팅 MyBatis Mapper.
 * JPA로 표현하기 어려운 복잡한 검색 쿼리를 처리한다.
 * XML 매퍼: resources/mapper/chat/ChatMapper.xml
 */
@Mapper
public interface ChatMapper {

    /**
     * 채팅방 검색.
     * 채팅방 제목(title) 또는 해당 방의 메시지 내용(content)에 검색어가 포함된 채팅방 목록을 반환한다.
     * use_yn = 'Y' 인 활성 채팅방만 조회한다.
     *
     * @param adminId 검색할 사용자 아이디
     * @param keyword 검색어 (title 또는 message content에 LIKE 적용)
     * @return 검색 결과 채팅방 목록
     */
    List<ChatRoom> searchRooms(@Param("adminId") String adminId, @Param("keyword") String keyword);
}
