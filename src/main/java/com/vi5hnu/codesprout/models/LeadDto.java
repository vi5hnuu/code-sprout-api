package com.vi5hnu.codesprout.models;

import com.vi5hnu.codesprout.commons.Constants;
import com.vi5hnu.codesprout.enums.LeadType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LeadDto {
    @NotBlank(message = "please enter a valid name")
    private String fullName;

    @NotBlank(message = "please enter a valid email")
    @Pattern(regexp = Constants.EMAIL_PATTERN)
    private String email;
    private String phone;
    @NotBlank(message = "please enter a valid subject") private String subject;
    @NotBlank(message = "please enter a valid message") private String message;
    private LeadType source = LeadType.PORTFOLIO;
}
