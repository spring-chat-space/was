package com.chat.was.admin.service;

import com.chat.was.admin.vo.*;

/**
 * 사용자 관리 서비스 인터페이스 (WAS 레이어).
 * 사용자 목록 조회, 단건 조회, 수정, 잠금 해제, 비밀번호 초기화, 삭제 기능을 제공한다.
 */
public interface AdminService {

    /**
     * 조건 + 페이징 적용 사용자 목록 조회.
     *
     * @param request 검색 조건 및 페이징 파라미터
     * @return 페이징 처리된 사용자 목록
     */
    AdminUserListResponseVo getUsers(AdminUserListRequestVo request);

    /**
     * 사용자 단건 상세 조회.
     *
     * @param adminId 조회할 사용자 아이디
     * @return 사용자 상세 정보
     */
    AdminUserDetailVo getUser(String adminId);

    /**
     * 사용자 정보 수정.
     *
     * @param adminId 수정할 사용자 아이디
     * @param request 수정 요청 데이터
     */
    void updateUser(String adminId, AdminUserUpdateRequestVo request);

    /**
     * 계정 잠금 해제. 로그인 실패 횟수와 잠금 시간을 초기화한다.
     *
     * @param adminId 잠금 해제할 사용자 아이디
     */
    void unlockUser(String adminId);

    /**
     * 비밀번호 초기화. 랜덤 임시 비밀번호를 생성하고 BCrypt 암호화 후 DB에 저장한다.
     *
     * @param adminId 비밀번호를 초기화할 사용자 아이디
     * @return 생성된 평문 임시 비밀번호 (화면에 1회 표시 후 폐기)
     */
    String resetPassword(String adminId);

    /**
     * 사용자 논리 삭제 (use_yn = 'N' 처리).
     *
     * @param adminId 삭제할 사용자 아이디
     */
    void deleteUser(String adminId);

    /**
     * 새 사용자 계정 생성.
     * 아이디 중복 체크 후 BCrypt 암호화 및 JPA 저장을 수행한다.
     * role이 지정된 경우 해당 권한으로 설정, 지정되지 않으면 ROLE_USER 사용.
     *
     * @param request 생성 요청 데이터 (adminId, password, adminName, email, phoneNumber, role)
     */
    void createUser(AdminUserCreateRequestVo request);
}
