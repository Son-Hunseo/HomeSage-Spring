package com.ssafy.homesage.domain.user.controller;

import java.util.List;

import com.ssafy.homesage.domain.user.model.dto.*;
import com.ssafy.homesage.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 모든 유저 반환
    @GetMapping
    private ResponseEntity<?> getFirstUser() {
        List<UserTestResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

}
