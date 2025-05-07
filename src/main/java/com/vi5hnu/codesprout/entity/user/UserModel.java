package com.vi5hnu.codesprout.entity.user;

import com.vi5hnu.codesprout.models.UserRole;
import com.vi5hnu.codesprout.models.UserDto;
import com.vi5hnu.codesprout.commons.Constants;
import com.vi5hnu.codesprout.utils.IdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = UserModel.TABLE_NAME)
public class UserModel {
    public final static String TABLE_NAME = "users";

    @Id
    private String id;

    @Column(name = "profile_url") private String profileUrl;
    @Column(name = "first_name") private String firstName;
    @Column(name = "last_name") private String lastName;
    @Column(name = "username") private String username;
    @Column(name = "email") private String email;
    @Column(name = "password") private String password;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "roles",columnDefinition = "json")
    @Builder.Default()
    private Set<UserRole> roles=Set.of(UserRole.ROLE_USER);

    @Column(name = "is_locked")
    @Builder.Default()
    private boolean isLocked=false; // is blocked or not

    @Column(name = "is_enabled")
    @Builder.Default()
    private boolean isEnabled=false; //is verified or not

    @Column(name = "is_deleted")
    @Builder.Default()
    private boolean isDeleted=false; //deleted user cannot be re-activated [new account is possible - new entry]

    @CreationTimestamp
    @Column(name = "created_at") private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at") private Timestamp updatedAt;

    @Column(name = "password_updated_at")
    private Timestamp passwordUpdatedAt;

    public static UserDto toDto(UserModel userModel){
        return UserDto.builder()
                .id(userModel.getId())
                .firstName(userModel.getFirstName())
                .profileUrl(userModel.getProfileUrl())
                .lastName(userModel.getLastName())
                .username(userModel.getUsername())
                .email(userModel.getEmail())
                .isLocked(userModel.isLocked())
                .isEnabled(userModel.isEnabled())
                .isDeleted(userModel.isDeleted())
                .createdAt(userModel.getCreatedAt())
                .updatedAt(userModel.getUpdatedAt())
                .passwordUpdatedAt(userModel.getPasswordUpdatedAt())
                .roles(userModel.getRoles())
                .build();
    }

    @PrePersist()
    private void beforeSave(){
        if(getId()==null) setId(IdGenerators.generateIdWithPrefix(Constants.USER_ID_PREFIX));
    }
}