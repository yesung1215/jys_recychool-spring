package com.app.recychool.service;

import com.app.recychool.domain.dto.UserPasswordUpdateDTO;
import com.app.recychool.domain.dto.UserUpdateDTO;
import com.app.recychool.domain.entity.User;
import com.app.recychool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserModifyImpl implements UserModify {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void updateUserInfo(Long userId, UserUpdateDTO updateDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (updateDTO.getUserName() != null && !updateDTO.getUserName().isBlank()) {
            user.setUserName(updateDTO.getUserName());
        }

        if (updateDTO.getUserPhone() != null && !updateDTO.getUserPhone().isBlank()) {
            user.setUserPhone(updateDTO.getUserPhone());
        }

        if (updateDTO.getUserPassword() != null && !updateDTO.getUserPassword().isBlank()) {
            user.setUserPassword(passwordEncoder.encode(updateDTO.getUserPassword()));
        }
    }

    @Override
    public void updatePassword(Long userId, UserPasswordUpdateDTO dto) {

        // 1. 유저 조회 (영속 상태)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // 2. 현재 비밀번호 검증
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getUserPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 3. 새 비밀번호 암호화 후 저장
        user.setUserPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

}
