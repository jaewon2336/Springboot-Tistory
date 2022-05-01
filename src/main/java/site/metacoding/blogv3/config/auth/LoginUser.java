package site.metacoding.blogv3.config.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import site.metacoding.blogv3.domain.user.User;

@Data
@RequiredArgsConstructor
public class LoginUser implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 로직
        return null;
    }

    @Override
    public String getPassword() {
        // 우리는 userDetail.getUser.getPassword 하면 되는데 우리가 아닌 시큐리티에게 필요
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // false이면 락걸림!!
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
