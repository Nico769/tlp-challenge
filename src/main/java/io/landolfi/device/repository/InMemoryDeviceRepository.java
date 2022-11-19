package io.landolfi.device.repository;

import io.landolfi.device.DeviceDto;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class InMemoryDeviceRepository implements DeviceRepository<DeviceDto> {


    private final Map<UUID, DeviceDto> devices = Collections.synchronizedMap(new HashMap<>());

    @Override
    public DeviceDto save(DeviceDto device) {
        devices.put(UUID.fromString(device.uuid()), device);
        return device;
    }

    @Override
    public void deleteAll() {
        devices.clear();
    }

    @Override
    public List<DeviceDto> findAll() {
        return devices.values().stream().toList();
    }
}
