package com.chat.was.ocr.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 영수증 OCR 분석 결과 VO.
 * Gemini Structured Outputs 응답을 역직렬화하며 WEB 레이어로 반환되는 최종 결과 포맷이다.
 */
@Getter
@NoArgsConstructor
public class OcrResultVo {

    /** 영수증 여부 (true: 유효한 영수증, false: 판독 불가 또는 비영수증) */
    @JsonProperty("is_receipt")
    private Boolean isReceipt;

    /** 오류 메시지 (is_receipt=false 시 한국어 오류 내용, 정상 시 null) */
    @JsonProperty("error_message")
    private String errorMessage;

    /** 가맹점 정보 (is_receipt=false 시 null) */
    @JsonProperty("merchant")
    private MerchantVo merchant;

    /** 거래 일시 (YYYY-MM-DD HH:mm:ss 포맷, is_receipt=false 시 null) */
    @JsonProperty("transaction_date")
    private String transactionDate;

    /** 결제 정보 목록 (분할 결제 지원, is_receipt=false 시 빈 배열) */
    @JsonProperty("payments")
    private List<PaymentVo> payments;

    /** 총 결제 합계 금액 (is_receipt=false 시 null) */
    @JsonProperty("total_amount")
    private Integer totalAmount;
}
