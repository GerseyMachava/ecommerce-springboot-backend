package com.ecommerce.backend.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.backend.model.base.BaseEntity;
import com.ecommerce.backend.model.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
    @Column(unique = true)
    @Email
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean enabled = true;
    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean locked = false;

    @OneToMany(mappedBy = "user")
    List<Address> adress;

    @OneToMany(mappedBy = "user")
    List<Orders> order;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));

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
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
