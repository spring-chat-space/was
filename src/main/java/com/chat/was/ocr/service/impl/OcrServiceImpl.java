package com.chat.was.ocr.service.impl;

import com.chat.was.global.config.GeminiConfig;
import com.chat.was.ocr.service.OcrService;
import com.chat.was.ocr.vo.MerchantVo;
import com.chat.was.ocr.vo.OcrResultVo;
import com.chat.was.ocr.vo.PaymentVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 영수증 OCR 서비스 구현체.
 * Gemini 2.5 Flash Vision API를 Structured Outputs 모드로 호출하여 영수증 정보를 추출한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final RestClient geminiRestClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper;

    private static final String GEMINI_MODEL = "/v1beta/models/gemini-2.5-flash:generateContent";

    /**
     * 영수증 이미지 분석 프롬프트.
     * 사업자 번호 형식 변환, 분할 결제, 일시불/할부 처리 지침 포함.
     */
    private static final String RECEIPT_PROMPT =
            "이 이미지에서 영수증 정보를 분석해주세요.\n" +
            "- 영수증이 맞는 경우: is_receipt=true로 설정하고 모든 필드를 최대한 채워주세요.\n" +
            "- 영수증이 아니거나 텍스트가 심하게 흐려 판독이 불가능한 경우: is_receipt=false로 설정하고 " +
            "error_message에 구체적인 문제 원인을 한국어로 작성해주세요.\n" +
            "- 사업자 등록번호(biz_number)는 반드시 '000-00-00000' 형태로 변환해주세요 (예: 1234567890 → 123-45-67890).\n" +
            "- 분할 결제의 경우 payments 배열에 결제 건수만큼 각 항목을 추가해주세요.\n" +
            "- 일시불은 installment=0, 할부 개월을 알 수 없으면 installment=null로 설정해주세요.\n" +
            "- 금액은 쉼표를 제거한 정수(integer)로 반환해주세요.\n" +
            "- 거래 일시는 'YYYY-MM-DD HH:mm:ss' 형식으로 반환해주세요.";

    /**
     * {@inheritDoc}
     * 이미지를 Base64로 인코딩하여 Gemini Vision API에 전달하고,
     * Structured Outputs(responseSchema)로 고정된 JSON 포맷의 응답을 받는다.
     *
     * @param image 영수증 이미지 파일
     * @return OCR 분석 결과 VO
     */
    @Override
    public OcrResultVo analyzeReceipt(MultipartFile image) {
        try {
            byte[] imageBytes = image.getBytes();
            String base64Data = Base64.getEncoder().encodeToString(imageBytes);
            String mimeType = (image.getContentType() != null) ? image.getContentType() : "image/jpeg";

            Map<String, Object> requestBody = buildRequestBody(base64Data, mimeType);
            String url = GEMINI_MODEL + "?key=" + geminiConfig.getGeminiApiKey();

            @SuppressWarnings("unchecked")
            Map<String, Object> response = geminiRestClient.post()
                    .uri(url)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            String jsonText = extractText(response);
            log.info("Gemini OCR 응답 JSON: {}", jsonText);
            return objectMapper.readValue(jsonText, OcrResultVo.class);

        } catch (Exception e) {
            log.error("영수증 OCR 분석 오류: {}", e.getMessage(), e);
            return buildErrorResult("영수증 분석 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    /**
     * Gemini API 요청 바디를 구성한다.
     * inlineData 파트로 Base64 이미지를, 텍스트 파트로 프롬프트를 포함시킨다.
     *
     * @param base64Data Base64 인코딩된 이미지 데이터
     * @param mimeType   이미지 MIME 타입 (예: image/jpeg)
     * @return Gemini API 요청 Map
     */
    private Map<String, Object> buildRequestBody(String base64Data, String mimeType) {
        Map<String, Object> inlineData = Map.of(
                "mimeType", mimeType,
                "data", base64Data
        );

        List<Map<String, Object>> parts = List.of(
                Map.of("inlineData", inlineData),
                Map.of("text", RECEIPT_PROMPT)
        );

        List<Map<String, Object>> contents = List.of(
                Map.of("parts", parts)
        );

        Map<String, Object> generationConfig = Map.of(
                "responseMimeType", "application/json",
                "responseSchema", buildResponseSchema()
        );

        return Map.of(
                "contents", contents,
                "generationConfig", generationConfig
        );
    }

    /**
     * Gemini Structured Outputs용 responseSchema를 구성한다.
     * 스키마는 OcrResultVo의 JSON 필드 구조와 일치해야 한다.
     *
     * @return Gemini responseSchema Map
     */
    private Map<String, Object> buildResponseSchema() {
        Map<String, Object> merchantProperties = new LinkedHashMap<>();
        merchantProperties.put("biz_number", Map.of("type", "STRING", "nullable", true));
        merchantProperties.put("name", Map.of("type", "STRING", "nullable", true));
        merchantProperties.put("address", Map.of("type", "STRING", "nullable", true));
        merchantProperties.put("tel", Map.of("type", "STRING", "nullable", true));

        Map<String, Object> merchantSchema = new LinkedHashMap<>();
        merchantSchema.put("type", "OBJECT");
        merchantSchema.put("nullable", true);
        merchantSchema.put("properties", merchantProperties);

        Map<String, Object> paymentItemProperties = new LinkedHashMap<>();
        paymentItemProperties.put("card_issuer", Map.of("type", "STRING", "nullable", true));
        paymentItemProperties.put("approval_number", Map.of("type", "STRING", "nullable", true));
        paymentItemProperties.put("installment", Map.of("type", "INTEGER", "nullable", true));
        paymentItemProperties.put("amount", Map.of("type", "INTEGER", "nullable", true));

        Map<String, Object> paymentsSchema = new LinkedHashMap<>();
        paymentsSchema.put("type", "ARRAY");
        paymentsSchema.put("nullable", true);
        paymentsSchema.put("items", Map.of("type", "OBJECT", "properties", paymentItemProperties));

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("is_receipt", Map.of("type", "BOOLEAN"));
        properties.put("error_message", Map.of("type", "STRING", "nullable", true));
        properties.put("merchant", merchantSchema);
        properties.put("transaction_date", Map.of("type", "STRING", "nullable", true));
        properties.put("payments", paymentsSchema);
        properties.put("total_amount", Map.of("type", "INTEGER", "nullable", true));

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "OBJECT");
        schema.put("properties", properties);
        schema.put("required", List.of("is_receipt"));
        return schema;
    }

    /**
     * Gemini API 응답 Map에서 텍스트(JSON 문자열)를 추출한다.
     *
     * @param response Gemini API 응답 Map
     * @return 추출된 텍스트 (JSON 형태의 문자열)
     */
    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }

    /**
     * 오류 상황에서 반환할 기본 OcrResultVo를 생성한다.
     * is_receipt=false, 나머지 필드는 null 또는 빈 배열로 설정한다.
     *
     * @param message 사용자에게 전달할 한국어 오류 메시지
     * @return 오류 상태의 OcrResultVo
     */
    private OcrResultVo buildErrorResult(String message) {
        OcrResultVo result = new OcrResultVo();
        // 리플렉션 없이 필드 설정을 위해 Jackson ObjectMapper 활용
        try {
            String errorJson = objectMapper.writeValueAsString(Map.of(
                    "is_receipt", false,
                    "error_message", message,
                    "payments", List.of()
            ));
            return objectMapper.readValue(errorJson, OcrResultVo.class);
        } catch (Exception ex) {
            log.error("오류 결과 생성 실패: {}", ex.getMessage());
            return result;
        }
    }
}
