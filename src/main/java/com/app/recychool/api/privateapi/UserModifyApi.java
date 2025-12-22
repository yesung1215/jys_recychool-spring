package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.UserUpdateDTO;
import com.app.recychool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")

public class UserModifyApi {
    private final UserService userService;

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateMyInfo(
            @PathVariable("id") Long userId,
            @RequestBody UserUpdateDTO userUpdateDTO) {

        userService.updateUserInfo(userId, userUpdateDTO);
        return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
    }
}
