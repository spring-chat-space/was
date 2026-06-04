package com.chat.was.ocr.controller;

import com.chat.was.global.common.ApiResponse;
import com.chat.was.ocr.service.OcrService;
import com.chat.was.ocr.vo.OcrResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 영수증 OCR REST API 컨트롤러.
 * WEB 레이어에서 전달된 영수증 이미지를 수신하고 Gemini OCR 분석 결과를 반환한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
public class OcrApiController {

    private final OcrService ocrService;

    /**
     * 영수증 이미지 OCR 분석 API.
     * multipart/form-data로 이미지를 수신하여 Gemini Vision API로 분석 후 결과를 반환한다.
     *
     * @param image 영수증 이미지 파일 (JPEG, PNG 등)
     * @return OCR 분석 결과 (가맹점 정보, 결제 내역, 총액 포함)
     */
    @PostMapping(value = "/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OcrResultVo>> analyzeReceipt(
            @RequestPart("image") MultipartFile image) {
        log.info("영수증 OCR 분석 요청 - 파일명: {}, 크기: {} bytes",
                image.getOriginalFilename(), image.getSize());
        OcrResultVo result = ocrService.analyzeReceipt(image);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
