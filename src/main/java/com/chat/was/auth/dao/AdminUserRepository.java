package com.chat.was.auth.dao;

import com.chat.was.auth.vo.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * AdminUser 엔티티 JPA Repository.
 * 단건 조회, 등록 등 단순 CRUD는 JPA를 통해 처리한다.
 */
public interface AdminUserRepository extends JpaRepository<AdminUser, String> {

    /**
     * 아이디로 관리자 사용자 조회.
     *
     * @param adminId 조회할 관리자 아이디
     * @return 조회된 AdminUser Optional 객체
     */
    Optional<AdminUser> findByAdminId(String adminId);

    /**
     * 아이디 중복 여부 확인.
     *
     * @param adminId 확인할 관리자 아이디
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByAdminId(String adminId);
}
