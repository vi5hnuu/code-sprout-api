package com.vi5hnu.codesprout.entity.user;

import com.vi5hnu.codesprout.commons.Constants;
import com.vi5hnu.codesprout.enums.AccountType;
import com.vi5hnu.codesprout.utils.IdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = UserAuthProviderModel.TABLE_NAME,
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "provider_user_id"})
        })
public class UserAuthProviderModel {
    public final static String TABLE_NAME = "user_auth_provider";

    @Id
    private String id;

    @Column(name = "user_id",nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider",nullable = false)
    private AccountType accountType;

    @Column(name = "provider_user_id",nullable = false)
    private String providerUserId;

    @CreationTimestamp
    @Column(name = "created_at") private Timestamp createdAt;

    @PrePersist()
    private void beforeSave(){
        if(getId()==null) setId(IdGenerators.generateIdWithPrefix(Constants.USER_AUTH_PROVIDER_ID_PREFIX));
    }
}