package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.model.dto.UserGetResponseDto;
import java.util.List;

public interface UserService {

    List<UserGetResponseDto> getAllUsers();
}
