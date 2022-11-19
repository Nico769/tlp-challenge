package io.landolfi.device.repository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository<T> {
    T save(T device);

    void deleteAll();

    List<T> findAll();

    Optional<T> findByUuid(String uuid);
}
