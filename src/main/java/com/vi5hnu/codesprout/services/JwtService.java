package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.Dto.user.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    public String generateJwtToken(UserDto user, long jwtExpirationMilliSeconds, String jwtSecret) {

        final Date curDate=new Date();
        final Date expireDate=new Date(curDate.getTime()+jwtExpirationMilliSeconds);
        return Jwts.builder()
                .setSubject(user.getId())
                .claim("first_name",user.getFirstName())
                .claim("last_name",user.getLastName())
                .claim("username",user.getUsername())
                .claim("email",user.getEmail())
                .claim("is_verified",user.isEnabled())
                .claim("created_at",user.getCreatedAt())
                .claim("roles",user.getRoles())
                .setIssuedAt(curDate)
                .setExpiration(expireDate)
                .signWith(key(jwtSecret), SignatureAlgorithm.HS256).compact();
    }

    public Claims getClaims(String token,Key jwtSecret){
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key key(String secret){
        final byte[] decodedSecret= Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(decodedSecret);
    }

    public String getTokenFromRequest(HttpServletRequest request){
        final List<Cookie> matchedCookies=request.getCookies()==null ? Collections.emptyList() : Arrays.stream(request.getCookies()).filter(ck->ck.getName().equals("jwt")).toList();
        String token= matchedCookies.isEmpty() ? null : matchedCookies.getFirst().getValue();
        if(token!=null && !token.isEmpty()) return token;

        String bearerTokenRaw=request.getHeader("Authorization");
        if(bearerTokenRaw==null || !bearerTokenRaw.startsWith("Bearer")){return null;}
        return bearerTokenRaw.substring(7);
    }
}
