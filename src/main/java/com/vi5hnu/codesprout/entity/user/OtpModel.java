package com.vi5hnu.codesprout.entity.user;

import com.vi5hnu.codesprout.commons.Constants;
import com.vi5hnu.codesprout.enums.OtpReason;
import com.vi5hnu.codesprout.enums.OtpStatus;
import com.vi5hnu.codesprout.security.RequestContext;
import com.vi5hnu.codesprout.utils.IdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = OtpModel.TABLE_NAME)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpModel {
    public static final String TABLE_NAME="otp";
    public static final int EXPIRE_AFTER_MINS=15;

    @Id
    private String id;

    @Column(nullable = false)
    private String otp;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "expire_at", nullable = false,updatable = false)
    private Timestamp expireAt;

    @Column(name = "ip_address", nullable = false,updatable = false)
    private String ipAddress;

    @Column(name = "user_agent", nullable = false,updatable = false)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default()
    private OtpStatus status = OtpStatus.UN_USED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,updatable = false)
    private OtpReason reason;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at",nullable = false)
    @UpdateTimestamp
    private Timestamp updatedAt;

    @PrePersist()
    private void beforeSave(){
        if(getId()==null) setId(IdGenerators.generateIdWithPrefix(Constants.OTP_ID_PREFIX));
        if(ipAddress==null) setIpAddress(RequestContext.getIpAddress());
        if(userAgent==null) setUserAgent(RequestContext.getUserAgent());
    }

}
