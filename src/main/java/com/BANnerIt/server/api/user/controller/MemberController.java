package com.BANnerIt.server.api.user.controller;

import com.BANnerIt.server.api.user.dto.MemberResponse;
import com.BANnerIt.server.api.user.dto.MemberUpdateRequest;
import com.BANnerIt.server.api.user.service.MemberService;
import com.BANnerIt.server.global.auth.JwtTokenUtil;
import com.BANnerIt.server.global.exception.ApiResponse;
import com.BANnerIt.server.global.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    public MemberController(MemberService memberService, JwtTokenUtil jwtTokenUtil) {
        this.memberService = memberService;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @GetMapping("/userdetail")
    public ResponseEntity<ApiResponse<MemberResponse>> getUserDetails(
            @RequestHeader("Authorization") final String authorizationHeader) {

        final Long userId = memberService.extractUserId(authorizationHeader);

        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ErrorCode.INVALID_TOKEN));
        }

        final MemberResponse response = memberService.getUserDetails(userId);

        if (response == null) {
            return ResponseEntity.status(ErrorCode.NOT_FOUND_MEMBER.getHttpStatus())
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND_MEMBER));
        }

        return ResponseEntity.ok()
                .header("Authorization", authorizationHeader)
                .body(ApiResponse.success(response));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateUser(
            @Valid @RequestBody MemberUpdateRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {

        final Long userId = memberService.extractUserId(authorizationHeader);

        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ErrorCode.INVALID_TOKEN));
        }

        final boolean isUpdated = memberService.updateUser(userId, request);

        if (!isUpdated) {
            return ResponseEntity.status(ErrorCode.NOT_FOUND_MEMBER.getHttpStatus())
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND_MEMBER));
        }

        return ResponseEntity.ok()
                .header("Authorization", authorizationHeader)
                .body(ApiResponse.success("회원정보가 수정되었습니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authorizationHeader) {

        final String token = authorizationHeader.replace("Bearer ", "");

        final boolean isLoggedOut = memberService.logout(token);

        if (!isLoggedOut) {
            return ResponseEntity.status(ErrorCode.NOT_FOUND_MEMBER.getHttpStatus())
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND_MEMBER));
        }

        return ResponseEntity.ok(ApiResponse.success("로그아웃 완료되었습니다."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @RequestHeader("Authorization") String authorizationHeader) {

        final String token = authorizationHeader.replace("Bearer ", "");

        final boolean isDeleted = memberService.deleteMember(token);

        if (!isDeleted) {
            return ResponseEntity.status(ErrorCode.NOT_FOUND_MEMBER.getHttpStatus())
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND_MEMBER));
        }

        return ResponseEntity.ok(ApiResponse.success("회원탈퇴가 완료되었습니다."));
    }
}
