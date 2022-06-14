package com.example.demo.repository;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.io.IOException;
import java.util.List;


public interface UserRepository extends JpaRepository <User, Long> {

     User findUserByUsername(String username);

     User findUserByEmail(String email);

}
