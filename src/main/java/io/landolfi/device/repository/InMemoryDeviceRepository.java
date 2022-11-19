package io.landolfi.device.repository;

import io.landolfi.device.DeviceDto;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
}
