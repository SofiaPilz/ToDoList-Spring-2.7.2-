package com.sofiapilz.todosimple.security;

import com.sofiapilz.todosimple.models.enums.ProfileEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class UserSpringSecurity implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserSpringSecurity(Long id, String username, String password, Set<ProfileEnum> profileEnums) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = profileEnums.stream().map(x -> new SimpleGrantedAuthority(x.getDescription())).collect(Collectors.toSet());
    }

    //nao permite q uma conta expire
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //nao permite ter contas travadas
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //n permite ter credenciais expiradas
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //nao permite ter contas desativadas
    @Override
    public boolean isEnabled() {
        return true;
    }

    //verifica as autoridades dos perfils
    public boolean hasRole(ProfileEnum profileEnum) {
        return getAuthorities().contains(new SimpleGrantedAuthority(profileEnum.getDescription()));
    }

}
