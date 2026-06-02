package com.chat.was.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 채팅방 목록 조회 응답 VO.
 * 사이드바에 채팅방 목록을 렌더링할 때 사용한다.
 */
@Getter
@AllArgsConstructor
public class ChatRoomVo {

    /** 채팅방 고유 ID */
    private Long roomId;

    /** 채팅방 제목 */
    private String title;

    /** 최근 수정 일시 */
    private LocalDateTime updatedAt;

    /** 생성 일시 */
    private LocalDateTime createdAt;
}
