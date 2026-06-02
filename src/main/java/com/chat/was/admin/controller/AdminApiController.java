package com.chat.was.admin.controller;

import com.chat.was.admin.service.AdminService;
import com.chat.was.admin.vo.*;
import com.chat.was.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 사용자 관리 REST API 컨트롤러 (WAS 레이어).
 * ROLE_ADMIN 권한 검사는 WEB 레이어에서 수행하므로 이 컨트롤러는 접근 제한 없이 허용.
 * 예외 처리는 GlobalExceptionHandler에서 일괄 처리.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    /**
     * 사용자 목록 조회 (조건 + 페이징).
     *
     * @param keyword  아이디 또는 이름 키워드 (선택)
     * @param role     권한 필터 (선택)
     * @param status   상태 필터: active | inactive | locked (선택)
     * @param page     페이지 번호 (0부터, 기본 0)
     * @param size     페이지당 건수 (기본 10)
     * @return 페이징 처리된 사용자 목록
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<AdminUserListResponseVo>> getUsers(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "role",    required = false) String role,
            @RequestParam(name = "status",  required = false) String status,
            @RequestParam(name = "page",    defaultValue = "0")  int page,
            @RequestParam(name = "size",    defaultValue = "10") int size) {

        log.info("사용자 목록 조회 API - keyword: {}, role: {}, status: {}, page: {}, size: {}",
                keyword, role, status, page, size);

        AdminUserListRequestVo request = new AdminUserListRequestVo();
        request.setKeyword(keyword);
        request.setRole(role);
        request.setStatus(status);
        request.setPage(page);
        request.setSize(size);

        AdminUserListResponseVo response = adminService.getUsers(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사용자 단건 상세 조회.
     *
     * @param adminId 조회할 사용자 아이디
     * @return 사용자 상세 정보
     */
    @GetMapping("/users/{adminId}")
    public ResponseEntity<ApiResponse<AdminUserDetailVo>> getUser(@PathVariable("adminId") String adminId) {
        log.info("사용자 상세 조회 API - adminId: {}", adminId);
        AdminUserDetailVo detail = adminService.getUser(adminId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    /**
     * 사용자 정보 수정.
     *
     * @param adminId 수정할 사용자 아이디
     * @param request 수정 요청 데이터
     * @return 성공 시 result=null
     */
    @PostMapping("/users/{adminId}/update")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @PathVariable("adminId") String adminId,
            @Valid @RequestBody AdminUserUpdateRequestVo request) {
        log.info("사용자 정보 수정 API - adminId: {}", adminId);
        adminService.updateUser(adminId, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 계정 잠금 해제.
     *
     * @param adminId 잠금 해제할 사용자 아이디
     * @return 성공 시 result=null
     */
    @PostMapping("/users/{adminId}/unlock")
    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable("adminId") String adminId) {
        log.info("계정 잠금 해제 API - adminId: {}", adminId);
        adminService.unlockUser(adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 비밀번호 초기화.
     * 임시 비밀번호를 생성하여 평문으로 반환한다 (1회 표시용).
     *
     * @param adminId 비밀번호를 초기화할 사용자 아이디
     * @return 생성된 임시 비밀번호
     */
    @PostMapping("/users/{adminId}/reset-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(@PathVariable("adminId") String adminId) {
        log.info("비밀번호 초기화 API - adminId: {}", adminId);
        String tempPassword = adminService.resetPassword(adminId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("tempPassword", tempPassword)));
    }

    /**
     * 사용자 논리 삭제 (use_yn = 'N').
     *
     * @param adminId 삭제할 사용자 아이디
     * @return 성공 시 result=null
     */
    @PostMapping("/users/{adminId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("adminId") String adminId) {
        log.info("사용자 삭제 API - adminId: {}", adminId);
        adminService.deleteUser(adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 신규 사용자 계정 생성.
     *
     * @param request 생성 요청 데이터 (adminId, password, adminName, email, phoneNumber, role)
     * @return 성공 시 result=null
     */
    @PostMapping("/users/create")
    public ResponseEntity<ApiResponse<Void>> createUser(
            @Valid @RequestBody AdminUserCreateRequestVo request) {
        log.info("신규 사용자 생성 API - adminId: {}", request.getAdminId());
        adminService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
