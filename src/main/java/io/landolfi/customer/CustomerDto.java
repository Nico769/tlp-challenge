package io.landolfi.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

// Let Jackson ignore fields with null values
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerDto(UUID uuid, String name, String surname, @JsonProperty("fiscal_code") String fiscalCode,
                          AddressDto address) {
    public CustomerDto(String name, String surname, String fiscalCode, AddressDto address) {
        this(null, name, surname, fiscalCode, address);
    }

}
