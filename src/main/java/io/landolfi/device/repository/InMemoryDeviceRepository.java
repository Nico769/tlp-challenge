package io.landolfi.device.repository;

import io.landolfi.device.DeviceDto;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class InMemoryDeviceRepository implements DeviceRepository<DeviceDto> {


    private final Map<UUID, DeviceDto> devices = Collections.synchronizedMap(new LinkedHashMap<>());

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

    @Override
    public Optional<DeviceDto> findByUuid(String uuid) {
        DeviceDto found = devices.get(UUID.fromString(uuid));
        if (found == null) {
            return Optional.empty();
        }
        return Optional.of(found);
    }

    @Override
    public void deleteById(String uuid) {
        devices.remove(UUID.fromString(uuid));
    }
}
