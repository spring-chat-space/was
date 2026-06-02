package com.chat.was.chat.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지 전송 요청 VO.
 * WEB → WAS 메시지 전송 API 요청 바디.
 * roomId가 null이면 신규 채팅방을 생성한다.
 */
@Getter
@NoArgsConstructor
public class ChatSendRequestVo {

    /** 요청 사용자 아이디 (WEB 레이어에서 세션에서 주입) */
    private String adminId;

    /** 기존 채팅방 ID (null이면 신규 방 생성) */
    private Long roomId;

    /** 사용자 입력 메시지 내용 */
    private String content;
}
