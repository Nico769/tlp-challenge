package io.landolfi.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

// Let Jackson ignore the `uuid` field only during deserialization because that field is generated server-side
@JsonIgnoreProperties(value = {"uuid"}, allowGetters = true)
public record CustomerDto(UUID uuid, String name, String surname, @JsonProperty("fiscal_code") String fiscalCode,
                          AddressDto address) {
    public CustomerDto(String name, String surname, String fiscalCode, AddressDto address) {
        this(null, name, surname, fiscalCode, address);
    }

}
