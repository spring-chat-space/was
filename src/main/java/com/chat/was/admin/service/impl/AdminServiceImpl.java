package com.chat.was.admin.service.impl;

import com.chat.was.admin.dao.AdminMapper;
import com.chat.was.admin.service.AdminService;
import com.chat.was.admin.vo.*;
import com.chat.was.auth.dao.AdminUserRepository;
import com.chat.was.auth.vo.AdminUser;
import com.chat.was.chat.dao.ChatMessageRepository;
import com.chat.was.chat.dao.ChatRoomRepository;
import com.chat.was.chat.vo.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 사용자 관리 비즈니스 로직 구현체 (WAS 레이어).
 * 목록 조회는 MyBatis(AdminMapper), 나머지 CRUD는 JPA(AdminUserRepository)를 사용한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final AdminUserRepository adminUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PasswordEncoder passwordEncoder;

    /** 임시 비밀번호 생성에 사용할 문자 집합 */
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final int TEMP_PASSWORD_LENGTH = 12;

    /**
     * {@inheritDoc}
     * MyBatis로 동적 조건 + 페이징 처리.
     */
    @Override
    @Transactional(readOnly = true)
    public AdminUserListResponseVo getUsers(AdminUserListRequestVo request) {
        log.info("사용자 목록 조회 - keyword: {}, role: {}, status: {}, page: {}, size: {}",
                request.getKeyword(), request.getRole(), request.getStatus(),
                request.getPage(), request.getSize());

        List<AdminUserListItemVo> items = adminMapper.findUsers(request);
        long totalCount = adminMapper.countUsers(request);
        int totalPages = (int) Math.ceil((double) totalCount / request.getSize());

        return new AdminUserListResponseVo(items, totalCount, totalPages, request.getPage(), request.getSize());
    }

    /**
     * {@inheritDoc}
     * JPA로 단건 조회, 없으면 예외 발생.
     */
    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailVo getUser(String adminId) {
        log.info("사용자 상세 조회 - adminId: {}", adminId);
        AdminUser adminUser = findAdminUserOrThrow(adminId);
        return new AdminUserDetailVo(adminUser);
    }

    /**
     * {@inheritDoc}
     * JPA로 사용자 정보 수정 후 저장.
     */
    @Override
    @Transactional
    public void updateUser(String adminId, AdminUserUpdateRequestVo request) {
        log.info("사용자 정보 수정 - adminId: {}", adminId);
        AdminUser adminUser = findAdminUserOrThrow(adminId);
        adminUser.updateInfo(request.getAdminName(), request.getEmail(),
                request.getPhoneNumber(), request.getRole(), request.getUseYn());
        adminUserRepository.save(adminUser);
        log.info("사용자 정보 수정 완료 - adminId: {}", adminId);
    }

    /**
     * {@inheritDoc}
     * JPA로 잠금 해제 처리.
     */
    @Override
    @Transactional
    public void unlockUser(String adminId) {
        log.info("계정 잠금 해제 - adminId: {}", adminId);
        AdminUser adminUser = findAdminUserOrThrow(adminId);
        adminUser.unlock();
        adminUserRepository.save(adminUser);
        log.info("계정 잠금 해제 완료 - adminId: {}", adminId);
    }

    /**
     * {@inheritDoc}
     * 비밀번호 정책(대문자+소문자+숫자+특수문자)을 충족하는 12자리 임시 비밀번호를 생성한다.
     */
    @Override
    @Transactional
    public String resetPassword(String adminId) {
        log.info("비밀번호 초기화 - adminId: {}", adminId);
        AdminUser adminUser = findAdminUserOrThrow(adminId);

        String tempPassword = generateTempPassword();
        adminUser.updatePassword(passwordEncoder.encode(tempPassword));
        adminUserRepository.save(adminUser);

        log.info("비밀번호 초기화 완료 - adminId: {}", adminId);
        return tempPassword;
    }

    /**
     * {@inheritDoc}
     * use_yn = 'D' 로 논리 삭제 처리.
     * 'N'(비활성)과 구분하여 목록 조회에서 영구적으로 제외된다.
     */
    @Override
    @Transactional
    public void deleteUser(String adminId) {
        log.info("사용자 논리 삭제 - adminId: {}", adminId);
        AdminUser adminUser = findAdminUserOrThrow(adminId);

        // 해당 사용자의 활성 채팅방을 모두 조회하여 메시지 물리 삭제 → 채팅방 논리 삭제
        List<ChatRoom> rooms = chatRoomRepository.findByUserSeqAndUseYnOrderByUpdatedAtDesc(adminUser.getUserSeq(), "Y");
        rooms.forEach(room -> chatMessageRepository.deleteAllByRoomId(room.getRoomId()));
        rooms.forEach(ChatRoom::delete);
        chatRoomRepository.saveAll(rooms);
        log.info("채팅방 {}개 연쇄 삭제 완료 - adminId: {}", rooms.size(), adminId);

        // 사용자 논리 삭제: use_yn = 'D' (비활성 'N'과 구분되는 삭제 상태)
        adminUser.updateInfo(adminUser.getAdminName(), adminUser.getEmail(),
                adminUser.getPhoneNumber(), adminUser.getRole(), "D");
        adminUserRepository.save(adminUser);
        log.info("사용자 논리 삭제 완료 - adminId: {}", adminId);
    }

    /**
     * 사용자 조회 공통 헬퍼. 없으면 IllegalArgumentException을 던진다.
     *
     * @param adminId 조회할 사용자 아이디
     * @return AdminUser 엔티티
     */
    private AdminUser findAdminUserOrThrow(String adminId) {
        return adminUserRepository.findByAdminId(adminId)
                .orElseThrow(() -> {
                    log.error("사용자 조회 실패 - 존재하지 않는 아이디: {}", adminId);
                    return new IllegalArgumentException("존재하지 않는 사용자입니다: " + adminId);
                });
    }

    /**
     * {@inheritDoc}
     * 신규 사용자 생성 전 아이디 중복 체크, BCrypt 암호화 후 저장.
     * role 파라미터가 비어있으면 ROLE_USER 기본값 사용.
     */
    @Override
    @Transactional
    public void createUser(AdminUserCreateRequestVo request) {
        log.info("신규 사용자 생성 - adminId: {}", request.getAdminId());

        // 아이디 중복 체크
        if (adminUserRepository.existsByAdminId(request.getAdminId())) {
            log.error("아이디 중복 - adminId: {}", request.getAdminId());
            throw new IllegalArgumentException("이미 존재하는 아이디입니다: " + request.getAdminId());
        }

        // 빈 전화번호 처리 (null -> 빈 문자열)
        String phoneNumber = request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()
                ? request.getPhoneNumber()
                : "";

        // Builder를 통해 AdminUser 생성 (기본 role: ROLE_USER, useYn: Y)
        AdminUser adminUser = AdminUser.builder()
                .adminId(request.getAdminId())
                .password(passwordEncoder.encode(request.getPassword()))
                .adminName(request.getAdminName())
                .email(request.getEmail())
                .phoneNumber(phoneNumber)
                .build();

        // role이 지정된 경우 (ROLE_ADMIN), updateInfo로 권한 변경
        String role = request.getRole();
        if (role != null && !role.isBlank() && "ROLE_ADMIN".equals(role)) {
            adminUser.updateInfo(adminUser.getAdminName(), adminUser.getEmail(),
                    adminUser.getPhoneNumber(), role, adminUser.getUseYn());
        }

        adminUserRepository.save(adminUser);
        log.info("신규 사용자 생성 완료 - adminId: {}, role: {}", request.getAdminId(),
                adminUser.getRole());
    }

    /**
     * 비밀번호 정책을 충족하는 랜덤 임시 비밀번호 생성.
     * 대문자 1개 이상, 소문자 1개 이상, 숫자 1개 이상, 특수문자 1개 이상 보장.
     *
     * @return 12자리 임시 비밀번호 (평문)
     */
    private String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        String allChars = UPPER + LOWER + DIGITS + SPECIAL;

        List<Character> chars = new ArrayList<>();
        // 각 필수 조건에서 최소 1개씩 추가
        chars.add(UPPER.charAt(random.nextInt(UPPER.length())));
        chars.add(LOWER.charAt(random.nextInt(LOWER.length())));
        chars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        chars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // 나머지 자리를 전체 문자 집합에서 무작위 선택
        for (int i = 4; i < TEMP_PASSWORD_LENGTH; i++) {
            chars.add(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 순서를 섞어 패턴 노출 방지
        Collections.shuffle(chars, random);

        StringBuilder sb = new StringBuilder();
        chars.forEach(sb::append);
        return sb.toString();
    }
}
