package com.example.demo.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ExecutionException;


import static java.util.concurrent.TimeUnit.MINUTES;


@Service
public class LoginAttemptService {

    private static final int MAXIMUM_NUM_OF_ATTEMPTS = 3;
    private static final int ATTEMPT_INCREMENT = 1;
    private final LoadingCache<String, Integer> loginAttemptCache;
    private final long LOCK_TIME_DURATION = 900000;
    private Date lockedDate;


    public LoginAttemptService() {
        super();
        this.loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    @Override
                    public @NotNull Integer load(@NotNull String key) throws Exception {
                        return 0;
                    }
                });
    }

    //removes user from the cache
    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    //add user to the cache
    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        //increments user login attempt
        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //adding user to the cache
        loginAttemptCache.put(username, attempts);
    }

    //check to see if user exceeded number of attempts
    public boolean userExceededNumberOfAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUM_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
