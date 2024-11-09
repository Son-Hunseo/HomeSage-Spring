package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.exception.DuplicatedEmailException;
import com.ssafy.homesage.domain.user.model.dto.UserGetResponseDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpRequestDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpResponseDto;

import java.util.List;

public interface UserService {

    List<UserGetResponseDto> getAllUsers();

    UserSignUpResponseDto signUp(UserSignUpRequestDto userSignUpRequestDto) throws DuplicatedEmailException;
}
