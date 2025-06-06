package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.annotation.RequireUserWith;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.entity.Lead;
import com.vi5hnu.codesprout.entity.user.UserModel;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.models.LeadDto;
import com.vi5hnu.codesprout.models.RoleDto;
import com.vi5hnu.codesprout.models.UserDto;
import com.vi5hnu.codesprout.repository.LeadRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/leads")
@RequiredArgsConstructor
public class LeadController {
    private final LeadRepository leadRepository;

    @PostMapping(path = "create-lead")
    public ResponseEntity<Map<String,Object>> createLead(@RequestBody @Valid LeadDto leadDto) throws ApiException {
        if(leadRepository.existsByEmail(leadDto.getEmail())) throw new ApiException(HttpStatus.BAD_REQUEST,"lead already created");
        final var lead=Lead.builder()
                .phone(leadDto.getPhone())
                .email(leadDto.getEmail())
                .source(leadDto.getSource())
                .subject(leadDto.getSubject())
                .message(leadDto.getMessage())
                .fullName(leadDto.getFullName())
                .build();
        leadRepository.save(lead);
        return ResponseEntity.status(200).body(Map.of("success",true,"message","Thanks for reaching me out...."));
    }
}