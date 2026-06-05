package com.chat.was.common.file.service;

import com.chat.was.common.file.vo.FileUploadResponseVo;
import com.chat.was.common.file.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;

public interface FileService {

    /**
     * 파일 업로드: 새 file_group 생성 후 파일들을 저장하고 fileGroupSeq 반환.
     *
     * @param groupType 도메인 구분 (GUIDE 등)
     * @param files     업로드 파일 목록
     * @return 파일 그룹 시퀀스와 업로드된 파일 목록
     */
    FileUploadResponseVo upload(String groupType, List<MultipartFile> files);

    /**
     * 파일 그룹에 속한 파일 목록 조회.
     *
     * @param fileGroupSeq 파일 그룹 시퀀스
     * @return 파일 VO 목록
     */
    List<FileVo> getFiles(Long fileGroupSeq);

    /**
     * 파일 다운로드용 InputStream 반환.
     *
     * @param fileSeq 파일 시퀀스
     * @return 파일 InputStream
     */
    InputStream download(Long fileSeq);

    /**
     * 파일 단건 조회.
     *
     * @param fileSeq 파일 시퀀스
     * @return FileVo
     */
    FileVo getFile(Long fileSeq);

    /**
     * 파일 논리 삭제 + 서버 물리 파일 삭제.
     *
     * @param fileSeq 삭제할 파일 시퀀스
     */
    void deleteFile(Long fileSeq);
}
