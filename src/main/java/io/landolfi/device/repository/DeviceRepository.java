package io.landolfi.device.repository;

public interface DeviceRepository<T> {
    T save(T device);

    void deleteAll();
}
