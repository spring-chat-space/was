package com.chat.was.admin.dao;

import com.chat.was.admin.vo.AdminUserListItemVo;
import com.chat.was.admin.vo.AdminUserListRequestVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 사용자 관리 MyBatis 매퍼 인터페이스.
 * 복잡한 동적 검색 + 페이징이 필요한 목록 조회를 담당한다.
 * XML: resources/mapper/admin/AdminMapper.xml
 */
@Mapper
public interface AdminMapper {

    /**
     * 조건 + 페이징 적용 사용자 목록 조회.
     *
     * @param request 검색 조건 및 페이징 파라미터
     * @return 현재 페이지 사용자 목록
     */
    List<AdminUserListItemVo> findUsers(AdminUserListRequestVo request);

    /**
     * 조건에 해당하는 전체 사용자 수 조회 (페이징 계산용).
     *
     * @param request 검색 조건 파라미터
     * @return 전체 건수
     */
    long countUsers(AdminUserListRequestVo request);
}
