package io.landolfi.device;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record DeviceDto(@NotBlank String uuid, @NotNull DeviceState state) {
}
