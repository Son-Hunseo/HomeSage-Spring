package com.ssafy.homesage.domain.user.model.jwt;

import lombok.Builder;

import java.util.Date;

@Builder
public record Token(String userEmail, String token, String hashedToken, Long issuedAt, Long expiration) {

    public Date getDateIssuedAt() {
        return new Date(this.issuedAt);
    }

    public Date getDateExpiration() {
        return new Date(this.expiration);
    }
}
