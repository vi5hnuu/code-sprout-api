package com.vi5hnu.codesprout.security;

import com.vi5hnu.codesprout.entity.user.UserModel;
import com.vi5hnu.codesprout.repository.UserRepository;
import com.vi5hnu.codesprout.specifications.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserModel userModel=userRepository.findOne(UserSpecifications.activeUserById(userId)).orElseThrow(()->new UsernameNotFoundException("user does not exists."));
        return new SecurityUserDetails(userModel);
    }
}