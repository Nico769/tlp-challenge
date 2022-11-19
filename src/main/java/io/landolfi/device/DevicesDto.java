package io.landolfi.device;

import javax.validation.Valid;
import java.util.List;

public record DevicesDto(@Valid List<DeviceDto> devices) {
}
