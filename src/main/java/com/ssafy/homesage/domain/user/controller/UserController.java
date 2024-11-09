package com.ssafy.homesage.domain.user.controller;

import java.util.List;

import com.ssafy.homesage.domain.user.model.dto.UserGetResponseDto;
import com.ssafy.homesage.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    private ResponseEntity<List<UserGetResponseDto>> getFirstUser(){
        List<UserGetResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }
}
