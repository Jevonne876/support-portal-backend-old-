package com.example.demo.listener;

import com.example.demo.Service.LoginAttemptService;
import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class AuthenticationSuccessListener {

    private final LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    //code below removes user from cache if then logged in successfully
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) throws ExecutionException {

        Object principal = event.getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal) {

            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }

    }
}
