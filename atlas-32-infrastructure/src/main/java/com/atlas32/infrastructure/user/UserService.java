package com.atlas32.infrastructure.user;

import com.atlas32.infrastructure.db.user.entity.UserEntity;
import com.atlas32.infrastructure.db.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserEntity registerUser(String username, String rawPassword) {
    if (userRepository.findByUsername(username).isPresent()) {
      throw new RuntimeException("Username already exists");
    }

    UserEntity user = UserEntity.builder()
        .username(username)
        .password(passwordEncoder.encode(rawPassword))
        .role("USER")
        .build();
    return userRepository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return org.springframework.security.core.userdetails.User
        .builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole())
        .build();
  }
}