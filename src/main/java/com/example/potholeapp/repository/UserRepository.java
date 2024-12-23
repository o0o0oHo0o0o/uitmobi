package com.example.potholeapp.repository;

import java.util.Optional;

import com.example.potholeapp.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

}
