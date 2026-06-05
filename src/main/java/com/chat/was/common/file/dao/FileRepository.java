package com.chat.was.common.file.dao;

import com.chat.was.common.file.vo.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    /**
     * 파일 그룹 내 활성 파일 목록을 생성 순서로 조회.
     *
     * @param fileGroupSeq 파일 그룹 시퀀스
     * @param useYn        사용 여부 ('Y'만 조회)
     * @return 파일 목록
     */
    List<FileEntity> findByFileGroupSeqAndUseYnOrderByCreatedAtAsc(Long fileGroupSeq, String useYn);

    /**
     * 파일 그룹 내 활성 파일 개수 조회.
     *
     * @param fileGroupSeq 파일 그룹 시퀀스
     * @param useYn        사용 여부 ('Y'만 카운트)
     * @return 파일 개수
     */
    long countByFileGroupSeqAndUseYn(Long fileGroupSeq, String useYn);
}
