package com.app.recychool.service;

import com.app.recychool.domain.dto.UserPasswordUpdateDTO;
import com.app.recychool.domain.dto.UserUpdateDTO;

public interface UserModify {
    public void updateUserInfo(Long userId, UserUpdateDTO userUpdateDTO);
    public void updatePassword(Long userId, UserPasswordUpdateDTO dto);
}
