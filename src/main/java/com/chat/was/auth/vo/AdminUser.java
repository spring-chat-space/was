package com.chat.was.auth.vo;

import com.chat.was.global.crypto.AES256Converter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 관리자 사용자 JPA 엔티티.
 * admin_user 테이블과 매핑되며, JPA Auditing으로 생성/수정 일시를 자동 관리한다.
 * email, phone_number 는 AES-256 양방향 암호화를 적용하여 DB에 저장한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "admin_user")
@EntityListeners(AuditingEntityListener.class)
public class AdminUser {

    /** 시퀀스 PK (AUTO_INCREMENT, Surrogate Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private Long userSeq;

    /** 관리자 로그인 아이디 (UNIQUE, 자연키로 강등) */
    @Column(name = "admin_id", nullable = false, length = 50, unique = true)
    private String adminId;

    /** BCrypt 단방향 암호화된 비밀번호 */
    @Column(name = "password", nullable = false)
    private String password;

    /** 관리자 이름 */
    @Column(name = "admin_name", nullable = false, length = 50)
    private String adminName;

    /** 이메일 (AES-256 암호화 저장) */
    @Convert(converter = AES256Converter.class)
    @Column(name = "email", nullable = false)
    private String email;

    /** 전화번호 (AES-256 암호화 저장) */
    @Convert(converter = AES256Converter.class)
    @Column(name = "phone_number", nullable = false, length = 100)
    private String phoneNumber;

    /** 계정 사용 여부 ('Y': 정상, 'N': 정지/탈퇴) */
    @Column(name = "use_yn", nullable = false, length = 1)
    private String useYn;

    /** 사용자 권한 (ROLE_USER, ROLE_ADMIN) */
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    /** 연속 로그인 실패 횟수 (5회 도달 시 계정 잠금) */
    @Column(name = "login_fail_count", nullable = false)
    private int loginFailCount;

    /** 계정 잠금 해제 시간 (null이면 잠금 없음) */
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    /** 계정 생성 일시 (JPA Auditing 자동 설정) */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 계정 최종 수정 일시 (JPA Auditing 자동 설정) */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 관리자 사용자 생성 빌더.
     * use_yn 기본값은 'Y'(정상), role 기본값은 'ROLE_USER'로 고정한다.
     *
     * @param adminId     관리자 아이디
     * @param password    BCrypt 암호화된 비밀번호
     * @param adminName   관리자 이름
     * @param email       이메일 (원문, JPA Converter가 암호화 처리)
     * @param phoneNumber 전화번호 (원문, JPA Converter가 암호화 처리)
     */
    @Builder
    public AdminUser(String adminId, String password, String adminName, String email, String phoneNumber) {
        this.adminId = adminId;
        this.password = password;
        this.adminName = adminName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.useYn = "Y";
        this.role = "ROLE_USER";
        this.loginFailCount = 0;
    }

    /**
     * 로그인 실패 처리.
     * failCount를 1 증가시키고, 5회 도달 시 1시간 계정 잠금을 설정한다.
     */
    public void increaseLoginFailCount() {
        this.loginFailCount++;
        // 5회 실패 시 현재 시간 기준 +1시간 잠금
        if (this.loginFailCount >= 5) {
            this.lockedUntil = LocalDateTime.now().plusHours(1);
        }
    }

    /**
     * 로그인 성공 시 실패 카운터 및 잠금 상태 초기화.
     */
    public void resetLoginState() {
        this.loginFailCount = 0;
        this.lockedUntil = null;
    }

    /**
     * 관리자가 사용자 정보를 수정할 때 호출.
     *
     * @param adminName   수정할 이름
     * @param email       수정할 이메일 (원문, JPA Converter가 암호화)
     * @param phoneNumber 수정할 전화번호 (원문, JPA Converter가 암호화)
     * @param role        수정할 권한 (ROLE_USER / ROLE_ADMIN)
     * @param useYn       수정할 활성화 여부 (Y / N)
     */
    public void updateInfo(String adminName, String email, String phoneNumber, String role, String useYn) {
        this.adminName = adminName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.useYn = useYn;
    }

    /**
     * 비밀번호 변경 (BCrypt 암호화된 값을 직접 전달받음).
     *
     * @param encodedPassword BCrypt 암호화된 새 비밀번호
     */
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * 계정 잠금 해제. 로그인 실패 횟수와 잠금 시간을 초기화한다.
     */
    public void unlock() {
        this.loginFailCount = 0;
        this.lockedUntil = null;
    }
}
