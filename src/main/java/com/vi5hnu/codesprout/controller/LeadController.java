package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.entity.Lead;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.models.ApiResponse;
import com.vi5hnu.codesprout.models.LeadDto;
import com.vi5hnu.codesprout.repository.LeadRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/leads")
@RequiredArgsConstructor
public class LeadController {
    private final LeadRepository leadRepository;

    @PostMapping(path = "create-lead")
    public ResponseEntity<ApiResponse<Void>> createLead(@RequestBody @Valid LeadDto leadDto) throws ApiException {
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
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Thanks for reaching me out...."));
    }
}
