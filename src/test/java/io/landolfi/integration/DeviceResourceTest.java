package io.landolfi.integration;

import io.landolfi.device.DeviceDto;
import io.landolfi.device.DeviceResource;
import io.landolfi.device.DeviceState;
import io.landolfi.device.repository.InMemoryDeviceRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.net.URI;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(DeviceResource.class)
class DeviceResourceTest {
    @TestHTTPEndpoint(DeviceResource.class)
    @TestHTTPResource
    URI devicesUri;

    @Inject
    InMemoryDeviceRepository deviceRepository;

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

}