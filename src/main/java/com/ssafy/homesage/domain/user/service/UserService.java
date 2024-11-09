package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.exception.DuplicatedEmailException;
import com.ssafy.homesage.domain.user.exception.LoginFailException;
import com.ssafy.homesage.domain.user.model.dto.*;

import java.util.List;

public interface UserService {

    List<UserTestResponseDto> getAllUsers();

    UserSignUpResponseDto signUp(UserSignUpRequestDto userSignUpRequestDto) throws DuplicatedEmailException;

    UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) throws LoginFailException;
}
