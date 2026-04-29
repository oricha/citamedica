package com.citamedica.backend.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Portal login principal: loads BCrypt-hashed password for DaoAuthenticationProvider
 * and exposes patient id for JWT claims.
 */
public class PatientUserDetails implements UserDetails {

    private final Long patientId;
    private final String email;
    private final String passwordHash;

    public PatientUserDetails(Long patientId, String email, String passwordHash) {
        this.patientId = patientId;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Long getPatientId() {
        return patientId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_PATIENT"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
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
}
