package com.chat.was.common.file.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

/**
 * 파일 저장소 추상화 인터페이스.
 * LocalFileStorageServiceImpl (로컬), S3FileStorageServiceImpl (S3) 중 @Profile로 전환 가능.
 */
public interface FileStorageService {

    /**
     * 파일을 저장하고 저장된 파일명(UUID 기반)을 반환.
     *
     * @param file      업로드된 MultipartFile
     * @param storedName UUID 기반으로 생성된 저장 파일명
     * @return 서버에 저장된 절대 경로
     */
    String store(MultipartFile file, String storedName);

    /**
     * 저장된 파일을 InputStream으로 반환.
     *
     * @param filePath 저장 파일 경로
     * @return 파일 InputStream
     */
    InputStream load(String filePath);

    /**
     * 저장된 파일을 서버에서 물리적으로 삭제.
     *
     * @param filePath 삭제할 파일 경로
     */
    void delete(String filePath);
}
