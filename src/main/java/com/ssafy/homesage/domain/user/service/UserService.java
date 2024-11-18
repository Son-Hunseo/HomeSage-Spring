package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.sale.model.dto.SaleResponseDto;
import com.ssafy.homesage.domain.user.model.dto.*;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<UserTestResponseDto> getAllUsers();

    void changedPassword(String accessToken, UserChangedPwRequestDto userChangedPwRequestDto);

    Map<String, Boolean> interest(String accessToken, Long saleId);

    List<InterestedSalesResponseDto> interestList(String accessToken);

    void reservation(String accessToken, ReserveRequestDto reserveRequestDto);

    void cancelReserve(String accessToken, Long saleId);

    List<ReserveResponseDto> reserveList(String accessToken);

    List<SaleResponseDto> providerSaleList(String accessToken);

    List<ReserveResponseDto> providerReserveList(String accessToken);
}
