package org.glocol.mallapi.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

import org.glocol.mallapi.util.CustomJwtException;
import org.glocol.mallapi.util.JWTUtil;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {
    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken) {
        if (refreshToken == null) {
            throw new CustomJwtException("NULL_REFREASH");
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJwtException("INVALID_STRING");
        }

        String accessToken = authHeader.substring(7);
        if (checkExpiredToken(accessToken) == false) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        String newAccessToken = JWTUtil.generateToken(claims, 10);
        String newRefreshToken = checkTime((Integer)claims.get("exp")) == true ?
                    JWTUtil.generateToken(claims, 60*24) : refreshToken;
        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    private boolean checkTime(Integer exp) {

        java.util.Date expDate = new java.util.Date((long)exp * (1000));
        long gap = expDate.getTime() - System.currentTimeMillis();
        long leftMin = gap / (1000 * 60);
        return leftMin < 60;

    }

    private boolean checkExpiredToken(String token) {
        try {
            JWTUtil.validateToken(token);
        } catch(CustomJwtException ex) {
            if (ex.getMessage().equals("Expired")) {
                return true;
            }
        }
        return false;
    }
    
}
