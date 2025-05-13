package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.models.RoleDto;
import com.vi5hnu.codesprout.models.UserDto;
import com.vi5hnu.codesprout.annotation.RequireUserWith;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.entity.user.UserModel;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.repository.OtpRepository;
import com.vi5hnu.codesprout.repository.UserRepository;
import com.vi5hnu.codesprout.repository.VerificationTokenRepository;
import com.vi5hnu.codesprout.services.GoogleService;
import com.vi5hnu.codesprout.services.JwtService;
import com.vi5hnu.codesprout.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    @Value("${app.jwt-secret}") private String jwtSecret;
    @Value("${app.jwt-expiration-milliseconds}") private int jwtExpireMs;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final OtpRepository otpRepository;
    private final ApplicationEventPublisher publisher;
    private final GoogleService googleService;
    private final JwtService jwtService;

    @GetMapping(path = "all")
    @RequireUserWith(isEnabled = true,isDeleted = false,isLocked = false)
    public ResponseEntity<Map<String,Object>> getUsers(@RequestParam(name = "pageNo",defaultValue = "1") int pageNo,@RequestParam(name = "count",defaultValue = "10") int count) throws ApiException {
        final Pageable<UserDto> userPageDto = userService.findAllUsers(pageNo, count);
        return ResponseEntity.ok(Map.of("success",true,"data",userPageDto));
    }

    @GetMapping(path = "{userId}")
    @RequireUserWith(isEnabled = true,isDeleted = false,isLocked = false)
    public ResponseEntity<Map<String,Object>> getUser(@PathVariable(name = "userId",required = true) String userId) throws ApiException {
        final var user = userService.getActiveUser(userId);
        return ResponseEntity.status(200).body(Map.of("success",true,"data",user));
    }
    @DeleteMapping(path = "{userId}")
    @RequireUserWith(isEnabled = true,isDeleted = false,isLocked = false)
    public ResponseEntity<Map<String,Object>> deleteUser(@PathVariable(name = "userId",required = true) String userId) throws ApiException, IOException {
        final var user = userService.deleteUserById(userId);
        return ResponseEntity.status(200).body(Map.of("success",true,"message",String.format("user %s deleted successfully.",user.getUsername())));
    }
    @PatchMapping(path = "add-role")
    @RequireUserWith(isEnabled = true,isDeleted = false,isLocked = false)
    public ResponseEntity<Map<String,Object>> addRole(@RequestBody @Valid RoleDto roleDto) throws ApiException {
        UserModel userModel = userService.updateRole(roleDto.getUserId(),roleDto.getRole());
        return ResponseEntity.status(200).body(Map.of("success",true,"message",String.format("role added for user %s", userModel.getUsername()),"data", UserModel.toDto(userModel)));
    }
}