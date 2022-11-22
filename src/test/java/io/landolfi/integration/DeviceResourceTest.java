package io.landolfi.integration;

import io.landolfi.customer.AddressDto;
import io.landolfi.customer.CustomerDto;
import io.landolfi.customer.repository.InMemoryCustomerRepository;
import io.landolfi.device.DeviceDto;
import io.landolfi.device.DeviceResource;
import io.landolfi.device.DeviceState;
import io.landolfi.device.repository.InMemoryDeviceRepository;
import io.landolfi.util.rest.ErrorDto;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(DeviceResource.class)
class DeviceResourceTest {
    @TestHTTPEndpoint(DeviceResource.class)
    @TestHTTPResource
    URI devicesUri;

    @Inject
    InMemoryDeviceRepository deviceRepository;

    @Inject
    InMemoryCustomerRepository customerRepository;

    @BeforeEach
    void beforeEach() {
        deviceRepository.deleteAll();
    }

    @Test
    void shouldCreateTheGivenDeviceSuccessfully_WhenPostingToTheEndpoint(){
        String deviceToCreateUuid = "7b787913-bda9-41dc-8966-458fe1e3c5ce";
        DeviceDto givenDevice = new DeviceDto(deviceToCreateUuid, DeviceState.ACTIVE);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(givenDevice)
        .when()
            .post()
        .then()
            .statusCode(201)
            .header(HttpHeaders.LOCATION, devicesUri.resolve(devicesUri.getPath() + "/" + deviceToCreateUuid).toString())
            .body("uuid", equalTo(deviceToCreateUuid))
            .body("state", equalTo(DeviceState.ACTIVE.toString()));
    }

    @Test
    void shouldRetrieveOneDeviceSuccessfully_WhenRequestingAllDevicesAndOneDeviceHasBeenPostedToTheEndpoint(){
        // Arrange
        String deviceToRetrieveUuid = "7b787913-bda9-41dc-8966-458fe1e3c5ce";
        DeviceDto givenDevice = new DeviceDto(deviceToRetrieveUuid, DeviceState.INACTIVE);

       given()
           .contentType(MediaType.APPLICATION_JSON)
           .body(givenDevice)
       .when()
           .post()
       .then()
           .statusCode(201);

        // Act and Assert
        when()
            .get()
        .then()
            .statusCode(200)
            .body("devices", hasSize(1))
            .body("devices[0].uuid", equalTo(deviceToRetrieveUuid))
            .body("devices[0].state", equalTo(DeviceState.INACTIVE.toString()));
    }

    @Test
    void shouldRetrieveAllDevicesSuccessfully_WhenRequestingAllDevicesAndMultipleDevicesHaveBeenPostedToTheEndpoint() {
        // Arrange
        String firstDeviceUuid = "7b787913-bda9-41dc-8966-458fe1e3c5ce";
        DeviceDto firstDevice = new DeviceDto(firstDeviceUuid, DeviceState.LOST);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(firstDevice)
        .when()
            .post()
        .then()
            .statusCode(201);

        String secondDeviceUuid = "8e24a4fd-9cfb-44ef-a94c-2a1692673665";
        DeviceDto secondDevice = new DeviceDto(secondDeviceUuid, DeviceState.INACTIVE);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(secondDevice)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Act and Assert
        when()
            .get()
        .then()
            .statusCode(200)
            .body("devices", hasSize(2))
            .body("devices[0].uuid", equalTo(firstDeviceUuid))
            .body("devices[0].state", equalTo(DeviceState.LOST.toString()))
            .body("devices[1].uuid", equalTo(secondDeviceUuid))
            .body("devices[1].state", equalTo(DeviceState.INACTIVE.toString()));
    }

    @Test
    void shouldRetrieveTheRequestedDeviceSuccessfully_WhenThatDeviceIsRequestedByUuidAndHasBeenPostedToTheEndpoint(){
        // Arrange
        String deviceToRetrieveUuid = "7b787913-bda9-41dc-8966-458fe1e3c5ce";
        DeviceDto givenDevice = new DeviceDto(deviceToRetrieveUuid, DeviceState.INACTIVE);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(givenDevice)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Act and Assert
        when()
            .get("/"+deviceToRetrieveUuid)
        .then()
            .statusCode(200)
            .body("devices", hasSize(1))
            .body("devices[0].uuid", equalTo(deviceToRetrieveUuid))
            .body("devices[0].state", equalTo(DeviceState.INACTIVE.toString()));
    }

    @Test
    void shouldNotRetrieveAnyDevice_WhenANonExistingDeviceIsRequested(){
        when()
            .get("/872cb98b-9106-4d26-acfa-083a62fd9727")
        .then()
            .statusCode(200)
            .body("devices", is(empty()));
    }

    @Test
    void shouldNotPerformAnyUpdateAndReturnNotFound_WhenTryingToUpdateANonExistingDevice(){
        // Arrange
        String nonExistingDeviceUuid = "872cb98b-9106-4d26-acfa-083a62fd9727";
        DeviceDto givenDevice = new DeviceDto(nonExistingDeviceUuid, DeviceState.LOST);

        // Act and Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(givenDevice)
        .when()
            .put("/" + nonExistingDeviceUuid)
        .then()
            .statusCode(404);

        // Make sure that the initial state of the repository hasn't been changed
        assertThat(deviceRepository.findAll()).isEmpty();
    }

    @Test
    void shouldRetrieveTheUpdatedDeviceSuccessfully_WhenUpdatingADeviceByState(){
        // Arrange
        String deviceToUpdateUuid = "7b787913-bda9-41dc-8966-458fe1e3c5ce";
        DeviceDto givenDevice = new DeviceDto(deviceToUpdateUuid, DeviceState.ACTIVE);
        deviceRepository.save(givenDevice);

        DeviceDto expectedDevice = new DeviceDto(deviceToUpdateUuid, DeviceState.LOST);

        // Act and Assert
        DeviceDto actualDevice =
                given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(expectedDevice)
                .when()
                    .put("/" + deviceToUpdateUuid)
                .then()
                    .statusCode(200)
                    .extract().body().as(DeviceDto.class);

        // Make sure that the updated device, returned within the PUT response,
        // is equal to the device to update that was provided to the PUT request
        assertThat(actualDevice).isEqualTo(expectedDevice);
        // Make sure that the updated device is stored successfully in the repository
        Optional<DeviceDto> storedDevice = deviceRepository.findByUuid(deviceToUpdateUuid);
        assertThat(storedDevice).get().isEqualTo(expectedDevice);
    }

    @Test
    void shouldNotPerformAnyUpdateAndReturnUnprocessableEntityAlongWithTheReason_WhenTryingToUpdateADeviceByUuid(){
        // Arrange
        String deviceToUpdateUuid = "7b787913-bda9-41dc-8966-458fe1e3c5ce";
        DeviceState givenState = DeviceState.ACTIVE;
        DeviceDto givenDevice = new DeviceDto(deviceToUpdateUuid, givenState);
        deviceRepository.save(givenDevice);

        DeviceDto unprocessableDevice = new DeviceDto("872cb98b-9106-4d26-acfa-083a62fd9727", givenState);

        String expectedReason = "A device cannot be updated by the following field(s): uuid";
        ErrorDto expectedError = new ErrorDto(expectedReason);

        // Act and Assert
        ErrorDto actualError =
                given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(unprocessableDevice)
                .when()
                    .put("/" + deviceToUpdateUuid)
                .then()
                    .statusCode(422)
                    .extract().body().as(ErrorDto.class);

        assertThat(actualError).isEqualTo(expectedError);
        // Make sure that the unprocessable device is NOT stored in the repository
        Optional<DeviceDto> storedDevice = deviceRepository.findByUuid(deviceToUpdateUuid);
        assertThat(storedDevice).get().isEqualTo(givenDevice);
    }

    @Test
    void shouldDeleteTheRequestedDeviceSuccessfullyAndReturnNoContent_WhenDeletingAnExistingDevice(){
        // Arrange
        String deviceToDeleteUuid = "7b787913-bda9-41dc-8966-458fe1e3c5ce";
        DeviceDto givenDevice = new DeviceDto(deviceToDeleteUuid, DeviceState.ACTIVE);
        deviceRepository.save(givenDevice);

        // Act and Assert
        when()
            .delete("/" + deviceToDeleteUuid)
        .then()
            .statusCode(204);

        // Make sure that the device is effectively deleted from the repository
        assertThat(deviceRepository.findAll()).isEmpty();
    }
    
    @Test
    void shouldNotPerformAnyDeleteAndReturnNoContent_WhenTryingToDeleteANonExistingDevice(){
        when()
            .delete("/872cb98b-9106-4d26-acfa-083a62fd9727")
        .then()
            .statusCode(204);

        // Make sure that the initial state of the repository hasn't been changed
        assertThat(deviceRepository.findAll()).isEmpty();
    }

    @Test
    void shouldRemoveTheAssociationBetweenACustomerAndADeletedDevice_WhenDeletingThatDevice() {
        // Arrange
        String associatedDeviceUuid = "7b787913-bda9-41dc-8966-458fe1e3c5ce";
        DeviceDto associatedDevice = new DeviceDto(associatedDeviceUuid, DeviceState.ACTIVE);
        deviceRepository.save(associatedDevice);

        AddressDto givenAddress = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        String givenCustomerUuid = "c8a255af-208d-4a98-bbff-8244a7a28609";
        CustomerDto givenCustomer = new CustomerDto(UUID.fromString(givenCustomerUuid), "Nicola", "Landolfi",
                "XFFTPK41D24B969W",
                givenAddress, List.of(associatedDevice));
        customerRepository.save(givenCustomer);

        // Act and Assert
        when()
            .delete("/" + associatedDeviceUuid)
        .then()
            .statusCode(204);

        // Make sure that the device is effectively deleted from the repository
        assertThat(deviceRepository.findAll()).isEmpty();

        // Make sure that the deleted device is no longer associated to the given customer
        Optional<CustomerDto> optStoredCustomer = customerRepository.findByUuid(givenCustomerUuid);
        CustomerDto storedCustomer = optStoredCustomer.orElseThrow(RuntimeException::new);
        assertThat(storedCustomer.devices()).isEmpty();
    }

    @Test
    void shouldUpdateTheDeviceAssociatedToACustomer_WhenUpdatingADeviceByState() {
        // Arrange
        String associatedDeviceUuid = "7b787913-bda9-41dc-8966-458fe1e3c5ce";
        DeviceDto associatedDevice = new DeviceDto(associatedDeviceUuid, DeviceState.ACTIVE);
        deviceRepository.save(associatedDevice);

        AddressDto givenAddress = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        String givenCustomerUuid = "c8a255af-208d-4a98-bbff-8244a7a28609";
        CustomerDto givenCustomer = new CustomerDto(UUID.fromString(givenCustomerUuid), "Nicola", "Landolfi",
                "XFFTPK41D24B969W",
                givenAddress, List.of(associatedDevice));
        customerRepository.save(givenCustomer);

        DeviceDto expectedDevice = new DeviceDto(associatedDeviceUuid, DeviceState.LOST);

        // Act and Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(expectedDevice)
        .when()
            .put("/" + associatedDeviceUuid)
        .then()
            .statusCode(200);

        // Make sure that the updated device is stored successfully in the repository
        Optional<DeviceDto> storedDevice = deviceRepository.findByUuid(associatedDeviceUuid);
        assertThat(storedDevice).get().isEqualTo(expectedDevice);

        // Make sure that the updated device is correctly associated to the customer
        CustomerDto storedCustomer =
                customerRepository.findByUuid(givenCustomerUuid).orElseThrow(RuntimeException::new);
        assertThat(storedCustomer.devices()).contains(expectedDevice);
    }
}