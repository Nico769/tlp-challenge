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
import static org.hamcrest.Matchers.equalTo;

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

}