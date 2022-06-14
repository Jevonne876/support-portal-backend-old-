package com.example.demo.resource;

import com.example.demo.Service.UserService;
import com.example.demo.domain.HttpResponse;
import com.example.demo.domain.User;
import com.example.demo.domain.UserPrincipal;
import com.example.demo.exception.domain.*;
import com.example.demo.exception.domain.ExceptionHandling;
import com.example.demo.utility.JWTTokenProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.example.demo.constant.FileConstant.*;
import static com.example.demo.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;


@RestController
public class UserResource extends ExceptionHandling {

    public static final String AN_EMAIL_WITH_NEW_PASSWORD_WAS_SENT_TO = "An email with new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully.";
    private final UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;


    @Autowired
    public UserResource(AuthenticationManager authenticationManager, UserService userService, JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("api/v1/post/login")
    @NotNull
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @PostMapping("api/v1/post/register")
    @NotNull
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistException, UsernameExistException {

        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername());

        return new ResponseEntity<>(newUser, OK);

    }

    @PostMapping("api/v1/post/add-new-user")
    public ResponseEntity<User> addNewUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isActive") String isActive,
            @RequestParam("isNonLocked") String isNonLocked,
            @RequestParam(value = "profileImg", required = false) MultipartFile profileImg)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {

        User newUser = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImg);
        return new ResponseEntity<>(newUser, OK);
    }


    @PutMapping("api/v1/put/update-user")
    public ResponseEntity<User> updateUser(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isActive") String isActive,
            @RequestParam("isNonLocked") String isNonLocked,
            @RequestParam(value = "profileImg", required = false) MultipartFile profileImg)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {
        User updatedUser = userService.updateUser(currentUsername, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImg);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("api/v1/get/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {

        User user = userService.findUserByUsername(username);

        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("api/v1/get/get-users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("api/v1/get/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(OK, AN_EMAIL_WITH_NEW_PASSWORD_WAS_SENT_TO + email);
    }

    @DeleteMapping("api/v1/delete/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(NO_CONTENT, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("api/v1/post/update-profile-img")
    public ResponseEntity<User> updateProfileImg(@RequestParam("username") String username, @RequestParam(value = "profileImg") MultipartFile profileImg) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {
        User user = userService.updateProfileImg(username, profileImg);
        return new ResponseEntity<>(user, OK);
    }

    //reads profile image stored on users computer
    @GetMapping(path = "/api/v1/get/image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImg(@PathVariable("username") String username, @PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));
    }

    //get temporary profile image
    @GetMapping(path = "api/v1/get/image/{profile}/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTemProfileImg(@PathVariable("profile") String profile, @PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMG_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            InputStream inputStream = url.openStream();
            byte[] chunk = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }

        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + username));
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }

    @NotNull
    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}