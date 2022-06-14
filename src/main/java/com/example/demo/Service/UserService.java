package com.example.demo.Service;


import com.example.demo.Service.impl.UserServiceImpl;
import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.enumeration.Roles;
import com.example.demo.exception.domain.*;
import com.example.demo.repository.UserRepository;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import static com.example.demo.constant.FileConstant.*;
import static com.example.demo.enumeration.Roles.ROLE_SUPER_ADMIN;
import static com.example.demo.enumeration.Roles.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;


@Service
@Transactional
@Qualifier("userDetailsService")
public class UserService implements UserDetailsService, UserServiceImpl {

    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String NO_USER_FOUND_BY_USERNAME = "No user found by username";
    public static final String RETURNING_FOUND_USER_BY_USERNAME = "Returning found user by username ";
    private static final String NO_USER_FOUND_BY_EMAIL = "NO user found by this email.";
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;
    private Date lockedDate;

    @Autowired
    public UserService(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository,
                       LoginAttemptService loginAttemptService, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + " " + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + " " + username);

        } else {
            validateLoginAttempt(user);
            user.setGetLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(RETURNING_FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String email, String username) throws EmailExistException, UsernameExistException, UserNotFoundException {

        validateNewUsernameAndEmail(EMPTY, email, username);
        User user = new User();

        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRoles(ROLE_SUPER_ADMIN.name());
        user.setPermissions(ROLE_SUPER_ADMIN.getPermissions());
        user.setProfileImgUrl(getTempProfileUrl(username));
        userRepository.save(user);
        LOGGER.info("New password " + password);
        System.out.println("New password " + password);
        //sends password to newly created user via email.
        try {
            emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImg)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {

        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRoles(getRoleEnumName(role).name());
        user.setPermissions(getRoleEnumName(role).getPermissions());
        user.setProfileImgUrl(getTempProfileUrl(username));
        userRepository.save(user);
        LOGGER.info("New password " + password);
        saveProfileImg(user, profileImg);
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile newProfileImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {

        User currentUser = userRepository.findUserByUsername(currentUsername);
        validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);

        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRoles(getRoleEnumName(role).name());
        currentUser.setPermissions(getRoleEnumName(role).getPermissions());
        userRepository.save(currentUser);
        saveProfileImg(currentUser, newProfileImg);
        return currentUser;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    @Override
    public void deleteUser(String username) throws IOException {
        User user = userRepository.findUserByUsername(username);
        Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepository.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL);
        } else {
            String password = generatePassword();
            user.setPassword(encodePassword(password));
            userRepository.save(user);
            emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
        }
    }

    @Override
    public User updateProfileImg(String username, MultipartFile newImg) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImg(user, newImg);
        return user;
    }

    private void saveProfileImg(User user, MultipartFile profileImg) throws IOException, NotAnImageFileException {
        if (profileImg != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE,IMAGE_GIF_VALUE).contains(profileImg.getContentType())){
                throw new NotAnImageFileException(profileImg.getOriginalFilename()+ " is not a image file, please upload an image.");
            }
            Path userFolder = Paths.get(USER_FOLDER + user.getFirstName()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(USER_FOLDER + user.getFirstName() + DOT + JPG_EXTENSION));
            Files.copy(profileImg.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImgUrl(setProfileImgUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImg.getOriginalFilename());
        }
    }

    private String setProfileImgUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();

    }

    private Roles getRoleEnumName(String role) {
        return Roles.valueOf(role.toUpperCase());
    }

    private String getTempProfileUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    //checks if user account is locked when logging in
    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.userExceededNumberOfAttempts(user.getUsername())) {
                lockedDate = new Date();
                System.out.println("locked date is: " + lockedDate);
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newEmail, String newUsername) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }
}
