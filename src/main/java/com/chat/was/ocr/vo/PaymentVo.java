package com.chat.was.ocr.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 영수증 결제 정보 VO.
 * 분할 결제를 지원하기 위해 payments 배열의 개별 항목으로 사용된다.
 */
@Getter
@NoArgsConstructor
public class PaymentVo {

    /** 카드사명 (예: 국민카드) */
    @JsonProperty("card_issuer")
    private String cardIssuer;

    /** 카드 승인번호 */
    @JsonProperty("approval_number")
    private String approvalNumber;

    /** 할부 개월 수 (일시불: 0, 알 수 없음: null) */
    @JsonProperty("installment")
    private Integer installment;

    /** 결제 금액 (쉼표 없는 정수) */
    @JsonProperty("amount")
    private Integer amount;
}
