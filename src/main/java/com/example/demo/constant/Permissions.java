package com.example.demo.constant;

public class Permissions {
    public static final String[] USER_PERMISSIONS = {"user:read",};
    public static final String[] HR_PERMISSIONS = {"user:read", ":user:update"};
    public static final String[] MANAGER_PERMISSIONS = {"user:read", "user:update"};
    public static final String[] ADMIN_PERMISSIONS = {"user:read", "user:create", "user:update"};
    public static final String[] SUPER_ADMIN_PERMISSIONS = {"user:read", "user:create", "user:update", "user:delete"};
}
