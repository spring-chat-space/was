package com.chat.was.common.file.service.impl;

import com.chat.was.common.file.dao.FileGroupRepository;
import com.chat.was.common.file.dao.FileRepository;
import com.chat.was.common.file.service.FileService;
import com.chat.was.common.file.service.FileStorageService;
import com.chat.was.common.file.vo.*;
import com.chat.was.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 파일 업로드/조회/삭제 비즈니스 로직 구현체.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    /** 허용되는 파일 확장자 목록 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("md", "jpg", "jpeg", "png", "gif", "pdf");
    /** 한 번에 업로드할 수 있는 최대 파일 개수 */
    private static final int MAX_FILES_PER_GROUP = 5;
    /** 한 파일의 최대 크기 (10MB) */
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    private final FileGroupRepository fileGroupRepository;
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public FileUploadResponseVo upload(String groupType, List<MultipartFile> files) {
        // 파일 수 제한 검증
        if (files.size() > MAX_FILES_PER_GROUP) {
            throw new BusinessException("파일은 최대 " + MAX_FILES_PER_GROUP + "개까지 업로드할 수 있습니다.");
        }

        // 확장자 및 크기 검증
        for (MultipartFile file : files) {
            String ext = getExtension(file.getOriginalFilename());
            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                throw new BusinessException("허용되지 않는 파일 형식입니다: " + ext);
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new BusinessException("파일 크기는 10MB를 초과할 수 없습니다: " + file.getOriginalFilename());
            }
        }

        // 파일 그룹 생성
        FileGroup fileGroup = fileGroupRepository.save(FileGroup.builder()
                .groupType(groupType.toUpperCase())
                .build());

        // 파일 저장
        List<FileVo> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            String ext = getExtension(file.getOriginalFilename());
            String storedName = UUID.randomUUID() + "." + ext;
            String filePath = fileStorageService.store(file, storedName);

            FileEntity saved = fileRepository.save(FileEntity.builder()
                    .fileGroupSeq(fileGroup.getFileGroupSeq())
                    .originalName(file.getOriginalFilename())
                    .storedName(storedName)
                    .filePath(filePath)
                    .fileSize(file.getSize())
                    .fileExt(ext)
                    .build());

            savedFiles.add(toVo(saved));
        }

        log.info("파일 업로드 완료 - groupType: {}, fileGroupSeq: {}, count: {}",
                groupType, fileGroup.getFileGroupSeq(), files.size());
        return new FileUploadResponseVo(fileGroup.getFileGroupSeq(), savedFiles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileVo> getFiles(Long fileGroupSeq) {
        return fileRepository.findByFileGroupSeqAndUseYnOrderByCreatedAtAsc(fileGroupSeq, "Y")
                .stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream download(Long fileSeq) {
        FileEntity file = fileRepository.findById(fileSeq)
                .filter(f -> "Y".equals(f.getUseYn()))
                .orElseThrow(() -> new BusinessException("파일을 찾을 수 없습니다."));
        return fileStorageService.load(file.getFilePath());
    }

    @Override
    @Transactional(readOnly = true)
    public FileVo getFile(Long fileSeq) {
        FileEntity file = fileRepository.findById(fileSeq)
                .filter(f -> "Y".equals(f.getUseYn()))
                .orElseThrow(() -> new BusinessException("파일을 찾을 수 없습니다."));
        return toVo(file);
    }

    @Override
    @Transactional
    public void deleteFile(Long fileSeq) {
        FileEntity file = fileRepository.findById(fileSeq)
                .filter(f -> "Y".equals(f.getUseYn()))
                .orElseThrow(() -> new BusinessException("파일을 찾을 수 없습니다."));
        String filePath = file.getFilePath();
        file.delete();
        fileRepository.save(file);
        fileStorageService.delete(filePath);
        log.info("파일 삭제 완료 - fileSeq: {}", fileSeq);
    }

    /**
     * 파일명에서 확장자 추출 (소문자 반환).
     *
     * @param originalName 원본 파일명
     * @return 확장자
     */
    private String getExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            throw new BusinessException("파일 확장자를 확인할 수 없습니다.");
        }
        return originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * FileEntity를 FileVo로 변환.
     *
     * @param f FileEntity
     * @return FileVo
     */
    private FileVo toVo(FileEntity f) {
        return new FileVo(f.getFileSeq(), f.getFileGroupSeq(), f.getOriginalName(),
                f.getFileSize(), f.getFileExt(), f.getCreatedAt());
    }
}
