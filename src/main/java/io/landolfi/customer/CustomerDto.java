package io.landolfi.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

// Let Jackson ignore fields with null values
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerDto(UUID uuid, @NotBlank String name, @NotBlank String surname,
                          @JsonProperty("fiscal_code") @NotBlank String fiscalCode,
                          @NotNull @Valid AddressDto address) {
    public CustomerDto(String name, String surname, String fiscalCode, AddressDto address) {
        this(null, name, surname, fiscalCode, address);
    }
    
    public boolean isAnyImmutableFieldsDifferentFrom(CustomerDto other) {
        return !(uuid.equals(other.uuid())
                && name.equals(other.name())
                && surname.equals(other.surname())
                && fiscalCode.equals(other.fiscalCode()));
    }

}
