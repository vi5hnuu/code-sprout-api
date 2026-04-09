package com.vi5hnu.codesprout.security.filters;

import com.vi5hnu.codesprout.services.JwtService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${app.jwt-secret}") private String jwtSecret;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token=jwtService.getTokenFromRequest(request);
        if(token==null) {
            filterChain.doFilter(request,response);return;
        }
        //pass to next filter [username pass auth]

        //check validity of token
        final Claims claims= this.jwtService.getClaims(token,jwtService.key(jwtSecret));
        final String userId= claims.getSubject();//userId

        UserDetails userDetails =this.userDetailsService.loadUserByUsername(userId);//username is userId
        //authorities
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails.getUsername(),null,userDetails.getAuthorities());//authenticated user
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request,response);//done handle api request
    }



    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        // Only skip OPTIONS preflight — the filter must still run for public endpoints
        // so that a logged-in user's JWT is processed and principal is available.
        // permitAll() in SecurityConfig already makes the token optional for those paths;
        // skipping here entirely means principal is always null, breaking access-level checks.
        return HttpMethod.OPTIONS.name().equals(request.getMethod());
    }
}