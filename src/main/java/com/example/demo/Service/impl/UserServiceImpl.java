package com.example.demo.Service.impl;

import com.example.demo.domain.User;
import com.example.demo.exception.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;


public interface UserServiceImpl {

    User register(String firstName, String lastName, String email, String username) throws EmailExistException, UsernameExistException, UserNotFoundException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String username, String email,
                    String role, boolean isNonLocked, boolean isActive,
                    MultipartFile profileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail,
                       String role, boolean isNonLocked, boolean isActive,
                       MultipartFile newProfileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteUser(String username) throws IOException;

    void resetPassword(String email) throws EmailExistException, MessagingException, EmailNotFoundException;

    User updateProfileImg(String username, MultipartFile newImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

}
