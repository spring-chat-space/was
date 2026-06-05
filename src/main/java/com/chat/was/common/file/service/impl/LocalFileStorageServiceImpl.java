package com.chat.was.common.file.service.impl;

import com.chat.was.common.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;

/**
 * 로컬 파일 시스템 기반 파일 저장소 구현체.
 * file.upload-dir 프로퍼티 경로에 파일을 저장한다.
 */
@Slf4j
@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file, String storedName) {
        try {
            // 업로드 디렉토리가 없으면 생성
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            // 파일 저장
            Path targetPath = dirPath.resolve(storedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("파일 저장 완료: {}", targetPath.toString());
            return targetPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + storedName, e);
        }
    }

    @Override
    public InputStream load(String filePath) {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + filePath, e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            log.info("파일 물리 삭제 완료: {}", filePath);
        } catch (IOException e) {
            log.error("파일 물리 삭제 실패: {}", filePath, e);
        }
    }
}
