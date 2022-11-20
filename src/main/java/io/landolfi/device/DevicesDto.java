package io.landolfi.device;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

public record DevicesDto(@Valid List<DeviceDto> devices) {
    public static DevicesDto withOneDevice(DeviceDto device) {
        return new DevicesDto(Collections.singletonList(device));
    }

    public static DevicesDto empty() {
        return new DevicesDto(Collections.emptyList());
    }
}
