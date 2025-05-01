package com.vi5hnu.codesprout.security;

import com.vi5hnu.codesprout.entity.user.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SecurityUserDetails implements UserDetails {
    private final UserModel userModel;
    public SecurityUserDetails(UserModel userModel){this.userModel=userModel;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.userModel.getRoles().stream().map(role->new SimpleGrantedAuthority(role.toString())).toList();
    }

    @Override
    public String getPassword() {
        return this.userModel.getPassword();
    }

    @Override
    public String getUsername() {
        return this.userModel.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.userModel.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.userModel.isEnabled();
    }
}
