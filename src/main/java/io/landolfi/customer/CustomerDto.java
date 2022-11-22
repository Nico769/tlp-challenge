package io.landolfi.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.landolfi.device.DeviceDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

// Let Jackson ignore fields with null values
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerDto(UUID uuid, @NotBlank String name, @NotBlank String surname,
                          @JsonProperty("fiscal_code") @NotBlank String fiscalCode,
                          @NotNull @Valid AddressDto address,
                          @Valid List<DeviceDto> devices) {

    public static final int MAX_NUMBER_OF_ASSOCIATED_DEVICES = 2;

    public CustomerDto(String name, String surname, String fiscalCode, AddressDto address) {
        this(null, name, surname, fiscalCode, address, null);
    }

    public CustomerDto(String name, String surname, String fiscalCode, AddressDto address, List<DeviceDto> devices) {
        this(null, name, surname, fiscalCode, address, List.copyOf(devices));
    }

    public CustomerDto(UUID uuid, String name, String surname, String fiscalCode, AddressDto address) {
        this(uuid, name, surname, fiscalCode, address, null);
    }


    public boolean isAnyImmutableFieldsDifferentFrom(CustomerDto other) {
        if (devices == null) {
            return !(uuid.equals(other.uuid())
                    && name.equals(other.name())
                    && surname.equals(other.surname())
                    && fiscalCode.equals(other.fiscalCode()));
        }
        return !(uuid.equals(other.uuid())
                && name.equals(other.name())
                && surname.equals(other.surname())
                && fiscalCode.equals(other.fiscalCode())
                && devices.equals(other.devices()));
    }

    public boolean hasReachedTheMaximumNumberOfAssociatedDevices() {
        if (devices == null) {
           return false;
        }
        return devices.size() == MAX_NUMBER_OF_ASSOCIATED_DEVICES;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Override
    public List<DeviceDto> devices() {
        return devices;
    }
}
