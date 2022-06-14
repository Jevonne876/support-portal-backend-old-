package com.example.demo.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 432_000_000; // 5 days expressed in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String JEVONNE = "Get Arrays, LLC";
    public static final String JEVONNE_ADMIN = "User Management Portal";
    public static final String PERMISSIONS = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
   public static final String[] PUBLIC_URLS = { "/api/v1/post/login", "/api/v1/post/register", "/image/**"};
//    public static final String[] PUBLIC_URLS = { "/api/**"};
}