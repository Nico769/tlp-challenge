package io.landolfi.unit;

import io.landolfi.customer.AddressDto;
import io.landolfi.customer.CustomerDto;
import io.landolfi.device.DeviceDto;
import io.landolfi.device.DeviceState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerDtoTest {
    static Validator validator;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldNotAllowEmptyName() {
        // Arrange
        AddressDto address = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto withEmptyName = new CustomerDto("", "Landolfi", "XFFTPK41D24B969W", address);

        // Act
        var violations = validator.validate(withEmptyName);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldNotAllowEmptySurname() {
        // Arrange
        AddressDto address = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto withEmptySurname = new CustomerDto("Nicola", "", "XFFTPK41D24B969W", address);

        // Act
        var violations = validator.validate(withEmptySurname);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldNotAllowEmptyFiscalCode() {
        // Arrange
        AddressDto address = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto withEmptyFiscalCode = new CustomerDto("Nicola", "Landolfi", "", address);

        // Act
        var violations = validator.validate(withEmptyFiscalCode);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldNotAllowEmptyAddress() {
        // Arrange
        CustomerDto withEmptyAddress = new CustomerDto("Nicola", "Landolfi", "XFFTPK41D24B969W", null);

        // Act
        var violations = validator.validate(withEmptyAddress);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldAllowEmptyDevices() {
        // Arrange
        AddressDto address = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto withEmptyDevices = new CustomerDto("Nicola", "Landolfi", "XFFTPK41D24B969W", address);

        // Act
        var violations = validator.validate(withEmptyDevices);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldNotAllowADeviceRaisingAnyViolation() {
        // Arrange
        AddressDto address = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        List<DeviceDto> withOneViolation = List.of(new DeviceDto("", DeviceState.ACTIVE));
        CustomerDto withAnInvalidDevice = new CustomerDto("Nicola", "Landolfi", "XFFTPK41D24B969W", address,
                withOneViolation);

        // Act
        var violations = validator.validate(withAnInvalidDevice);

        // Assert
        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldNotAllowMultipleDevicesRaisingAnyViolation() {
        // Arrange
        AddressDto address = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        List<DeviceDto> withMultipleViolations = List.of(new DeviceDto("", DeviceState.ACTIVE),
                new DeviceDto("", DeviceState.ACTIVE));
        CustomerDto withMultipleInvalidDevices = new CustomerDto("Nicola", "Landolfi", "XFFTPK41D24B969W", address,
                withMultipleViolations);

        // Act
        var violations = validator.validate(withMultipleInvalidDevices);

        // Assert
        assertThat(violations).isNotEmpty();
    }
}