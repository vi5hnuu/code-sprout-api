package com.vi5hnu.codesprout.services.user;

import com.vi5hnu.codesprout.models.RegisterRequestDto;
import com.vi5hnu.codesprout.models.UserRole;
import com.vi5hnu.codesprout.models.UserDto;
import com.vi5hnu.codesprout.entity.user.UserModel;
import com.vi5hnu.codesprout.enums.AccountType;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.exceptions.UserAlreadyExistsException;
import com.vi5hnu.codesprout.repository.UserRepository;
import com.vi5hnu.codesprout.specifications.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserModel deleteUserById(String uid) throws ApiException {
        final var user=userRepository.findOne(UserSpecifications.activeUserById(uid)).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"User not found"));
        user.setDeleted(true);
        return userRepository.save(user);
    }

    
    public UserModel deleteUserByUsername(String username) throws ApiException {
        final var user=userRepository.findOne(UserSpecifications.activeUserByUsername(username)).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"User not found"));
        user.setDeleted(true);
        return userRepository.save(user);
    }

    
    public UserModel updatePassword(String username,String oldPassword,String newPassword,String confirmPassword) throws ApiException {
        final var user=userRepository.findOne(UserSpecifications.activeUserByUsername(username)).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"User not found"));
        if(!passwordEncoder.matches(oldPassword,user.getPassword())) throw new ApiException(HttpStatus.BAD_REQUEST,"incorrect password");
        if(!newPassword.equals(confirmPassword)) throw  new ApiException(HttpStatus.BAD_REQUEST,"new password must be equal to confirm password");
        if(passwordEncoder.matches(newPassword, user.getPassword())) throw new ApiException(HttpStatus.BAD_REQUEST,"old pasword and new password must be different");

        final String encryptedPassword=passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        user.setPasswordUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return userRepository.save(user);
    }

    
    public UserModel updatePasswordById(String userId, String oldPassword, String newPassword, String confirmPassword) throws ApiException {
        final var user=userRepository.findById(userId).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"User not found"));

        if(!passwordEncoder.matches(oldPassword,user.getPassword())) throw new ApiException(HttpStatus.BAD_REQUEST,"incorrect password");
        if(!newPassword.equals(confirmPassword)) throw  new ApiException(HttpStatus.BAD_REQUEST,"new password must be equal to confirm password");
        if(passwordEncoder.matches(newPassword, user.getPassword())) throw new ApiException(HttpStatus.BAD_REQUEST,"old pasword and new password must be different");

        final String encryptedPassword=passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        user.setPasswordUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return userRepository.save(user);
    }

    
    public UserModel updateRole(String uid, UserRole role) throws ApiException {
        final var user=userRepository.findById(uid).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"User not found"));
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    
    public com.vi5hnu.codesprout.commons.Pageable<UserDto> findAllUsers(int pageNo, int count) throws ApiException {//pageNo from 1 onwards
        if(pageNo<=0 || count<=0) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid pageNo [1,]/count");
        Pageable pageable = PageRequest.of(pageNo - 1, count); // Page index is 0-based in Spring Data
        final var users=userRepository.findAll(UserSpecifications.activeUsers(),pageable);
        final var totalUsers=userRepository.count(UserSpecifications.activeUsers());
        return new com.vi5hnu.codesprout.commons.Pageable<>(users.stream().map(UserModel::toDto).toList(),pageNo,totalUsers);
    }

    public UserDto getActiveUser(String userId) throws ApiException {//pageNo from 1 onwards
        final var user = userRepository.findOne(UserSpecifications.activeUserById(userId)).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"User not found"));
        return UserModel.toDto(user);
    }

    
    public UserModel createUser(RegisterRequestDto registerRequestDto) throws UserAlreadyExistsException, ApiException {
        final var exUser = userRepository.findOne(UserSpecifications.activeUserByUsernameOrEmail(registerRequestDto.getUserName(),registerRequestDto.getEmail(),null,null,false)).orElse(null);
        if(exUser!=null) throw new ApiException(HttpStatus.BAD_REQUEST,"Username/email already exists");

        final UserModel userModel= UserModel.builder()
                .accountType(AccountType.MANUAL)
                .email(registerRequestDto.getEmail())
                .firstName(registerRequestDto.getFirstName())
                .lastName(registerRequestDto.getLastName())
                .username(registerRequestDto.getUserName())
                .roles(Set.of(UserRole.ROLE_USER))
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .build();
        return userRepository.save(userModel);
    }

    public Optional<UserModel> findByUsernameOrEmail(String usernameEmail,Boolean isLocked,Boolean isDeleted,Boolean isEnabled){
        return userRepository.findOne(UserSpecifications.activeUserByUsernameOrEmail(usernameEmail,isEnabled,isLocked,isDeleted));
    }

    public Optional<UserModel>  findUserById(String userId) {
        return userRepository.findById(userId);
    }
}
