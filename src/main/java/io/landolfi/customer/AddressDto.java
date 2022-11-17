package io.landolfi.customer;

import javax.validation.constraints.NotBlank;

public record AddressDto(@NotBlank String street, @NotBlank String city, @NotBlank String province,
                         @NotBlank String region) {
}
