package com.chat.was.ocr.service;

import com.chat.was.ocr.vo.OcrResultVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 영수증 OCR 서비스 인터페이스.
 * Gemini Vision API를 통한 영수증 이미지 분석을 정의한다.
 */
public interface OcrService {

    /**
     * 영수증 이미지를 분석하여 결제 정보를 추출한다.
     *
     * @param image 영수증 이미지 파일 (JPEG, PNG 등)
     * @return OCR 분석 결과 (가맹점 정보, 결제 정보, 총액 포함)
     */
    OcrResultVo analyzeReceipt(MultipartFile image);
}
