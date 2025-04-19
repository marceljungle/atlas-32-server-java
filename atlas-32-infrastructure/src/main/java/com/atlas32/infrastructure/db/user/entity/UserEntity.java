package com.atlas32.infrastructure.db.user.entity;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import com.atlas32.infrastructure.db.device.entity.DeviceEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DeviceEntity> devices = new ArrayList<>();

  public UserEntity withApiKey(ApiKeyEntity apiKey) {
    return UserEntity.builder()
        .apiKey(apiKey)
        .id(this.id)
        .username(this.username)
        .password(this.password)
        .role(this.role)
        .build();
  }
}