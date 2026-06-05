package com.chat.was.common.file.dao;

import com.chat.was.common.file.vo.FileGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileGroupRepository extends JpaRepository<FileGroup, Long> {
}
