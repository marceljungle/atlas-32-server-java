package com.atlas32.infrastructure.db.api.key.repository;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Long> {

  List<ApiKeyEntity> findByName(String name);
}