package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

@Entity(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private String profileImgUrl;
    private Date lastLoginDate;
    private Date getLastLoginDateDisplay;
    private Date joinDate;
    private String roles;
    private String[] permissions;
    private boolean isActive;
    private boolean isNotLocked;

    public User() {
    }

    public User(Long id, String userId, String firstName, String lastName,
                String username, String password, String email, String profileImgUrl,
                Date lastLoginDate, Date getLastLoginDateDisplay, Date joinDate, String roles,
                String[] permissions, boolean isActive, boolean isNotLocked) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.lastLoginDate = lastLoginDate;
        this.getLastLoginDateDisplay = getLastLoginDateDisplay;
        this.joinDate = joinDate;
        this.roles = roles;
        this.permissions = permissions;
        this.isActive = isActive;
        this.isNotLocked = isNotLocked;
    }

    public User(String userId, String firstName, String lastName, String username, String password,
                String email, String profileImgUrl, Date lastLoginDate, Date getLastLoginDateDisplay,
                Date joinDate, String roles, String[] permissions, boolean isActive, boolean isNotLocked) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.lastLoginDate = lastLoginDate;
        this.getLastLoginDateDisplay = getLastLoginDateDisplay;
        this.joinDate = joinDate;
        this.roles = roles;
        this.permissions = permissions;
        this.isActive = isActive;
        this.isNotLocked = isNotLocked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getGetLastLoginDateDisplay() {
        return getLastLoginDateDisplay;
    }

    public void setGetLastLoginDateDisplay(Date getLastLoginDateDisplay) {
        this.getLastLoginDateDisplay = getLastLoginDateDisplay;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isNotLocked() {
        return isNotLocked;
    }

    public void setNotLocked(boolean notLocked) {
        isNotLocked = notLocked;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", profileImgUrl='" + profileImgUrl + '\'' +
                ", lastLoginDate=" + lastLoginDate +
                ", getLastLoginDateDisplay=" + getLastLoginDateDisplay +
                ", joinDate=" + joinDate +
                ", roles=" + roles +
                ", permissions=" + Arrays.toString(permissions) +
                ", isActive=" + isActive +
                ", isNotLocked=" + isNotLocked +
                '}';
    }
}
