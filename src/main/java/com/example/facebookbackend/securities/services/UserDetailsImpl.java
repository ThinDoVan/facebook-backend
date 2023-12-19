package com.example.facebookbackend.securities.services;

import com.example.facebookbackend.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
//    private Integer id;
//    private String fullname;
//    private String email;
//    private String password;
//    private Set<GrantedAuthority> grantedAuthoritySet;
//
//    public UserDetailsImpl(Integer id,
//                           String fullname,
//                           String email,
//                           String password,
//                           Set<GrantedAuthority> grantedAuthoritySet) {
//        this.id = id;
//        this.fullname = fullname;
//        this.email = email;
//        this.password = password;
//        this.grantedAuthoritySet = grantedAuthoritySet;
//    }
//
//    public static UserDetailsImpl build(User user) {
//        Set<GrantedAuthority> authoritySet = user.getRoleSet().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
//                .collect(Collectors.toSet());
//        return new UserDetailsImpl(user.getUserId(),
//                user.getFullName(),
//                user.getEmail(),
//                user.getPassword(),
//                authoritySet);
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return grantedAuthoritySet;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if(this==obj){
//            return true;
//        }if (obj==null||getClass()!=obj.getClass()){
//            return false;
//        }
//        UserDetailsImpl userDetails = (UserDetailsImpl) obj;
//        return Objects.equals(id, userDetails.id);
//    }
    private User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(user);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoleSet().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) obj;
        return Objects.equals(user.getUserId(), userDetails.user.getUserId());
    }
}
