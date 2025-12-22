package com.app.recychool.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordUpdateDTO {
    private String currentPassword; // 현재 비밀번호
    private String newPassword;     // 새 비밀번호
}
