package io.landolfi.device.repository;

import java.util.List;

public interface DeviceRepository<T> {
    T save(T device);

    void deleteAll();

    List<T> findAll();
}
