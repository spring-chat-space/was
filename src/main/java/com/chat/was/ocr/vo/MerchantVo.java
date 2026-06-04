package com.chat.was.ocr.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 영수증 가맹점 정보 VO.
 * Gemini OCR 응답의 merchant 객체를 역직렬화하는 데 사용된다.
 */
@Getter
@NoArgsConstructor
public class MerchantVo {

    /** 사업자 등록번호 (000-00-00000 형태) */
    @JsonProperty("biz_number")
    private String bizNumber;

    /** 가맹점(식당/가게) 이름 */
    @JsonProperty("name")
    private String name;

    /** 가맹점 주소 */
    @JsonProperty("address")
    private String address;

    /** 가맹점 전화번호 */
    @JsonProperty("tel")
    private String tel;
}
