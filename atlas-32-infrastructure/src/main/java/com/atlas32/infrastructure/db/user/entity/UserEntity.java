package com.atlas32.infrastructure.db.user.entity;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import com.atlas32.infrastructure.db.device.entity.DeviceEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DeviceEntity> devices = new ArrayList<>();

  public UserEntity withApiKey(ApiKeyEntity apiKey) {
    return UserEntity.builder()
        .apiKey(apiKey)
        .id(this.id)
        .username(this.username)
        .password(this.password)
        .role(this.role)
        .devices(this.devices)
        .build();
  }
}