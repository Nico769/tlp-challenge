package io.landolfi.unit;

import io.landolfi.customer.AddressDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;

class AddressDtoTest {
    static Validator validator;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldNotAllowEmptyStreetAddress() {
        // Arrange
        AddressDto withEmptyStreet = new AddressDto("", "Padova", "Padova", "Veneto");

        // Act
        var violations = validator.validate(withEmptyStreet);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldNotAllowEmptyCity() {
        // Arrange
        AddressDto withEmptyCity = new AddressDto("Via fasulla 10", "", "Padova", "Veneto");

        // Act
        var violations = validator.validate(withEmptyCity);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldNotAllowEmptyProvince() {
        // Arrange
        AddressDto withEmptyProvince = new AddressDto("Via fasulla 10", "Caserta", "", "Campania");

        // Act
        var violations = validator.validate(withEmptyProvince);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldNotAllowEmptyRegion() {
        // Arrange
        AddressDto withEmptyRegion = new AddressDto("Via fasulla 10", "Caserta", "Napoli", "");

        // Act
        var violations = validator.validate(withEmptyRegion);

        // Assert
        assertThat(violations).isNotEmpty();
    }
}