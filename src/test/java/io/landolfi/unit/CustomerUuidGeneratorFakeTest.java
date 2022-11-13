package io.landolfi.unit;

import io.landolfi.customer.CustomerUuidGeneratorFake;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerUuidGeneratorFakeTest {
    @Test
    void shouldYieldAUuid_WhenNextIsInvoked() {
        // Arrange
        CustomerUuidGeneratorFake generator = new CustomerUuidGeneratorFake();
        String expected = "c8a255af-208d-4a98-bbff-8244a7a28609";

        // Act
        UUID actual = generator.next().orElseThrow();

        // Assert
        assertThat(actual).hasToString(expected);
    }

    @Test
    void shouldYieldTwoDifferentUuid_WhenNextIsInvokedTwice() {
        // Arrange
        CustomerUuidGeneratorFake generator = new CustomerUuidGeneratorFake();
        String firstExpected = "c8a255af-208d-4a98-bbff-8244a7a28609";
        String secondExpected = "12014578-3bd6-4fd8-9dc2-2e40f83831d2";

        // Act
        UUID firstActual = generator.next().orElseThrow();
        UUID secondActual = generator.next().orElseThrow();

        // Assert
        assertThat(firstActual).hasToString(firstExpected);
        assertThat(secondActual).hasToString(secondExpected);
    }
    
    @Test
    void shouldBringTheFakeBackToItsInitialState_WhenResetIsInvoked(){
        // Arrange
        CustomerUuidGeneratorFake generator = new CustomerUuidGeneratorFake();
        String firstExpected = "c8a255af-208d-4a98-bbff-8244a7a28609";
        String secondExpected = "12014578-3bd6-4fd8-9dc2-2e40f83831d2";

        // Act
        generator.next();
        generator.reset();

        // Assert
        assertThat(generator.next().orElseThrow()).hasToString(firstExpected);
        assertThat(generator.next().orElseThrow()).hasToString(secondExpected);

    }

}