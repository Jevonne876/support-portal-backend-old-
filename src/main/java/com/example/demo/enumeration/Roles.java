package com.example.demo.enumeration;

import static com.example.demo.constant.Permissions.*;

public enum Roles {

    ROLE_USER(USER_PERMISSIONS),
    ROLE_HR(HR_PERMISSIONS),
    ROLE_MANAGER(MANAGER_PERMISSIONS),
    ROLE_ADMIN(ADMIN_PERMISSIONS),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_PERMISSIONS);

    private String[] permissions;

    Roles(String... permissions) {
        this.permissions = permissions;
    }

    public String[] getPermissions() {
        return permissions;
    }
}
