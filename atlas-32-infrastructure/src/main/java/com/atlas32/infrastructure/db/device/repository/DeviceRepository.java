package com.atlas32.infrastructure.db.device.repository;

import com.atlas32.infrastructure.db.device.entity.DeviceEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {

  List<DeviceEntity> findAllByOwnerId(Long ownerId);
}