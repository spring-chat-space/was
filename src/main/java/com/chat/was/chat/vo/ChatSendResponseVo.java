package com.chat.was.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 채팅 메시지 전송 응답 VO.
 * AI 답변과 함께 roomId, 신규 채팅방 제목(최초 전송 시)을 반환한다.
 */
@Getter
@AllArgsConstructor
public class ChatSendResponseVo {

    /** 채팅방 ID (신규 생성 또는 기존 방) */
    private Long roomId;

    /** AI 응답 메시지 내용 */
    private String aiContent;

    /**
     * 채팅방 제목 (신규 채팅방의 첫 메시지인 경우에만 값이 들어옴).
     * null이면 제목 변경 없음.
     */
    private String roomTitle;
}
