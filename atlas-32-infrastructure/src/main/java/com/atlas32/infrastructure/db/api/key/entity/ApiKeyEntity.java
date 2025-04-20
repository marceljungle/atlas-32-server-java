package com.atlas32.infrastructure.db.api.key.entity;

import com.atlas32.infrastructure.db.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "api_keys")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApiKeyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String encryptedValue;

  private String salt;

  @OneToOne(mappedBy = "apiKey")
  private UserEntity user;
}