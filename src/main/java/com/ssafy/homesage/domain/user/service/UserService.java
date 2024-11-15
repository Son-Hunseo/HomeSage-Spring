package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.model.dto.*;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<UserTestResponseDto> getAllUsers();

    void changedPassword(String accessToken, UserChangedPwRequestDto userChangedPwRequestDto);

    Map<String, Boolean> interest(String accessToken, Long saleId);
}
