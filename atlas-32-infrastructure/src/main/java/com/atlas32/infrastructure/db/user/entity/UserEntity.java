package com.atlas32.infrastructure.db.user.entity;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  private String password;

  private String role;

  @OneToOne
  @JoinColumn(name = "api_key_id")
  private ApiKeyEntity apiKey;

  public UserEntity withApiKey(ApiKeyEntity apiKey) {
    this.apiKey = apiKey;
    return this;
  }
}