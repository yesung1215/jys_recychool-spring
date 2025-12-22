package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.UserPasswordUpdateDTO;
import com.app.recychool.domain.dto.UserResponseDTO;
import com.app.recychool.domain.dto.UserUpdateDTO;
import com.app.recychool.service.UserModifyImpl;
import com.app.recychool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")

public class UserModifyApi {
    private final UserModifyImpl userModifyImpl;
    private final UserService userService;

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateMyInfo(
            @PathVariable("id") Long userId,
            @RequestBody UserUpdateDTO userUpdateDTO) {

        userModifyImpl.updateUserInfo(userId, userUpdateDTO);
        return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(
            @PathVariable Long id,
            @RequestBody UserPasswordUpdateDTO dto) {

        userModifyImpl.updatePassword(id, dto);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

}
