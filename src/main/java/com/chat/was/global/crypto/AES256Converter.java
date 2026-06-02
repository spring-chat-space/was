package com.chat.was.global.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES-256 양방향 암호화 JPA AttributeConverter.
 * email, phone_number 등 민감 개인정보를 DB 저장 시 자동 암호화, 조회 시 자동 복호화한다.
 * Spring Bean으로 등록되어 @Value를 통해 application.yml의 키/IV 값을 주입받는다.
 */
@Slf4j
@Component
@Converter(autoApply = false)
public class AES256Converter implements AttributeConverter<String, String> {

    /** AES-256 암호화에 사용할 32바이트 비밀 키 */
    @Value("${crypto.aes256.secret-key}")
    private String secretKey;

    /** AES CBC 모드에 사용할 16바이트 초기화 벡터(IV) */
    @Value("${crypto.aes256.iv}")
    private String iv;

    /**
     * 엔티티 속성값을 DB 컬럼값으로 변환 (암호화).
     *
     * @param attribute 원문 문자열
     * @return AES-256/CBC/PKCS5Padding 암호화 후 Base64 인코딩된 문자열
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("AES-256 암호화 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("암호화 처리 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * DB 컬럼값을 엔티티 속성값으로 변환 (복호화).
     *
     * @param dbData Base64 인코딩된 암호화 문자열
     * @return 복호화된 원문 문자열
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decoded = Base64.getDecoder().decode(dbData);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES-256 복호화 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("복호화 처리 중 오류가 발생했습니다.", e);
        }
    }
}
