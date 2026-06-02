package com.chat.was.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 응답 DTO.
 * 채팅 히스토리 조회 API 응답에 사용된다.
 */
@Getter
@AllArgsConstructor
public class ChatMessageVo {

    /** 메시지 고유 ID */
    private Long messageId;

    /** 소속 채팅방 ID */
    private Long roomId;

    /** 송신자 타입 ('USER' 또는 'AI') */
    private String senderType;

    /** 메시지 내용 */
    private String content;

    /** 메시지 생성 일시 */
    private LocalDateTime createdAt;
}
