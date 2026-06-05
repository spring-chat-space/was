package com.chat.was.common.file.controller;

import com.chat.was.common.file.service.FileService;
import com.chat.was.common.file.vo.FileUploadResponseVo;
import com.chat.was.common.file.vo.FileVo;
import com.chat.was.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 공통 파일 업로드/다운로드/삭제 REST API 컨트롤러.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileApiController {

    private final FileService fileService;

    /**
     * 파일 업로드 API.
     * multipart/form-data로 최대 5개 파일 수신, file_group을 생성하여 fileGroupSeq 반환.
     *
     * @param groupType 도메인 구분 (GUIDE 등)
     * @param files     업로드 파일 목록
     * @return 생성된 fileGroupSeq와 파일 목록
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponseVo>> upload(
            @RequestParam("groupType") String groupType,
            @RequestParam("files") List<MultipartFile> files) {
        log.info("파일 업로드 요청 - groupType: {}, count: {}", groupType, files.size());
        return ResponseEntity.ok(ApiResponse.success(fileService.upload(groupType, files)));
    }

    /**
     * 파일 그룹 목록 조회 API.
     *
     * @param fileGroupSeq 파일 그룹 시퀀스
     * @return 해당 그룹의 파일 목록
     */
    @GetMapping("/group/{fileGroupSeq}")
    public ResponseEntity<ApiResponse<List<FileVo>>> getGroupFiles(
            @PathVariable("fileGroupSeq") Long fileGroupSeq) {
        log.info("파일 그룹 조회 - fileGroupSeq: {}", fileGroupSeq);
        return ResponseEntity.ok(ApiResponse.success(fileService.getFiles(fileGroupSeq)));
    }

    /**
     * 파일 다운로드 API.
     * 파일을 바이너리 스트림으로 반환하며, Content-Disposition 헤더로 파일명을 UTF-8 인코딩하여 전달.
     *
     * @param fileSeq 다운로드할 파일 시퀀스
     * @return 파일 스트림 (Content-Disposition: attachment)
     */
    @GetMapping("/{fileSeq}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable("fileSeq") Long fileSeq) {
        log.info("파일 다운로드 요청 - fileSeq: {}", fileSeq);
        FileVo fileVo = fileService.getFile(fileSeq);
        InputStream is = fileService.download(fileSeq);
        // UTF-8 인코딩된 파일명으로 다운로드 제목 설정
        String encodedName = URLEncoder.encode(fileVo.getOriginalName(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(is));
    }

    /**
     * 파일 삭제 API (논리 삭제 + 물리 삭제).
     *
     * @param fileSeq 삭제할 파일 시퀀스
     * @return 200 OK
     */
    @PostMapping("/{fileSeq}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable("fileSeq") Long fileSeq) {
        log.info("파일 삭제 요청 - fileSeq: {}", fileSeq);
        fileService.deleteFile(fileSeq);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
