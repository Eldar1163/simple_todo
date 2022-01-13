package com.example.simple_todo.сonfig;

import com.example.simple_todo.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class TodoUserDetails implements UserDetails {
    private String username;
    private String password;

    public static TodoUserDetails fromUserToTodoUserDetails(User user) {
        TodoUserDetails todoUserDetails = new TodoUserDetails();
        todoUserDetails.username = user.getUsername();
        todoUserDetails.password = user.getPassword();

        return todoUserDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
