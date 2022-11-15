package io.landolfi.integration;

import io.landolfi.customer.AddressDto;
import io.landolfi.customer.CustomerDto;
import io.landolfi.customer.CustomerResource;
import io.landolfi.customer.repository.InMemoryCustomerRepository;
import io.landolfi.doubles.CustomerUuidGeneratorFake;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(CustomerResource.class)
class CustomerResourceTest {
    @TestHTTPEndpoint(CustomerResource.class)
    @TestHTTPResource
    URI customersUri;

    @Inject
    InMemoryCustomerRepository customerRepository;

    @Inject
    CustomerUuidGeneratorFake idGeneratorFake;

    @BeforeEach
    void beforeEach() {
        idGeneratorFake.reset();
        customerRepository.deleteAll();
    }

    @Test
    void shouldCreateTheGivenCustomerSuccessfullyIgnoringTheClientProvidedCustomerUuid_WhenPostingToTheEndpoint() {
        AddressDto givenAddress = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        // Assuming that the client is trying to provide a UUID even though he is not allowed to do so,
        // since the UUID must be generated by the server itself
        CustomerDto givenCustomer = new CustomerDto(UUID.randomUUID(), "Nicola", "Landolfi", "XFFTPK41D24B969W",
                givenAddress);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(givenCustomer)
        .when()
            .post()
        .then()
            .statusCode(201)
            .header(HttpHeaders.LOCATION,
                    customersUri.resolve(customersUri.getPath() + "/c8a255af-208d-4a98-bbff-8244a7a28609").toString())
            .body("uuid", equalTo("c8a255af-208d-4a98-bbff-8244a7a28609"))
            .body("name", equalTo("Nicola"))
            .body("surname", equalTo("Landolfi"))
            .body("fiscal_code", equalTo("XFFTPK41D24B969W"))
            .body("address.street", equalTo("Via fasulla 10"))
            .body("address.city", equalTo("Padova"))
            .body("address.province", equalTo("Padova"))
            .body("address.region", equalTo("Veneto"));
    }

    @Test
    void shouldCreateTheGivenCustomerSuccessfullyWithServerSideGeneratedCustomerUuid_WhenPostingToTheEndpoint() {
        AddressDto givenAddress = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto givenCustomer = new CustomerDto("Nicola", "Landolfi", "XFFTPK41D24B969W",
                givenAddress);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(givenCustomer)
        .when()
            .post()
        .then()
            .statusCode(201)
            .header(HttpHeaders.LOCATION,
                    customersUri.resolve(customersUri.getPath() + "/c8a255af-208d-4a98-bbff-8244a7a28609").toString())
            .body("uuid", equalTo("c8a255af-208d-4a98-bbff-8244a7a28609"))
            .body("name", equalTo("Nicola"))
            .body("surname", equalTo("Landolfi"))
            .body("fiscal_code", equalTo("XFFTPK41D24B969W"))
            .body("address.street", equalTo("Via fasulla 10"))
            .body("address.city", equalTo("Padova"))
            .body("address.province", equalTo("Padova"))
            .body("address.region", equalTo("Veneto"));
    }

    @Test
    void shouldRetrieveOneCustomerSuccessfully_WhenRequestingAllCustomersAndOneCustomerHasBeenPostedToTheEndpoint(){
        // Arrange
        AddressDto givenAddress = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto givenCustomer = new CustomerDto("Nicola", "Landolfi", "XFFTPK41D24B969W",
                givenAddress);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(givenCustomer)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Act and Assert
        when()
            .get()
        .then()
            .statusCode(200)
            .body("customers", hasSize(1))
            .body("customers[0].uuid", equalTo("c8a255af-208d-4a98-bbff-8244a7a28609"))
            .body("customers[0].name", equalTo("Nicola"))
            .body("customers[0].surname", equalTo("Landolfi"))
            .body("customers[0].fiscal_code", equalTo("XFFTPK41D24B969W"))
            .body("customers[0].address.street", equalTo("Via fasulla 10"))
            .body("customers[0].address.city", equalTo("Padova"))
            .body("customers[0].address.province", equalTo("Padova"))
            .body("customers[0].address.region", equalTo("Veneto"));
    }

    @Test
    void shouldRetrieveAllCustomersSuccessfully_WhenRequestingAllCustomersAndMultipleCustomersHaveBeenPostedToTheEndpoint() {
        // Arrange
        AddressDto firstCustomerAddress = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto firstCustomer = new CustomerDto("Nicola", "Landolfi", "XFFTPK41D24B969W",
                firstCustomerAddress);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(firstCustomer)
        .when()
            .post()
        .then()
            .statusCode(201);

        AddressDto secondCustomerAddress = new AddressDto("Via molto fasulla 20", "Milano", "Milano", "Lombardia");
        CustomerDto secondCustomer = new CustomerDto("Paolo", "Rossi", "XLIGLC74D19F768I", secondCustomerAddress);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(secondCustomer)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Act and Assert
        when()
            .get()
        .then()
            .statusCode(200)
            .body("customers", hasSize(2))
            .body("customers[0].uuid", equalTo("c8a255af-208d-4a98-bbff-8244a7a28609"))
            .body("customers[0].name", equalTo("Nicola"))
            .body("customers[0].surname", equalTo("Landolfi"))
            .body("customers[0].fiscal_code", equalTo("XFFTPK41D24B969W"))
            .body("customers[0].address.street", equalTo("Via fasulla 10"))
            .body("customers[0].address.city", equalTo("Padova"))
            .body("customers[0].address.province", equalTo("Padova"))
            .body("customers[0].address.region", equalTo("Veneto"))
            .body("customers[1].uuid", equalTo("12014578-3bd6-4fd8-9dc2-2e40f83831d2"))
            .body("customers[1].name", equalTo("Paolo"))
            .body("customers[1].surname", equalTo("Rossi"))
            .body("customers[1].fiscal_code", equalTo("XLIGLC74D19F768I"))
            .body("customers[1].address.street", equalTo("Via molto fasulla 20"))
            .body("customers[1].address.city", equalTo("Milano"))
            .body("customers[1].address.province", equalTo("Milano"))
            .body("customers[1].address.region", equalTo("Lombardia"));
    }

    @Test
    void shouldRetrieveTheRequestedCustomerSuccessfully_WhenThatCustomerIsRequestedByUuidAndHasBeenPostedToTheEndpoint(){
        // Arrange
        AddressDto givenAddress = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto givenCustomer = new CustomerDto("Nicola", "Landolfi", "XFFTPK41D24B969W",
                givenAddress);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(givenCustomer)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Act and Assert
        when()
            .get("/c8a255af-208d-4a98-bbff-8244a7a28609")
        .then()
            .statusCode(200)
            .body("customers", hasSize(1))
            .body("customers[0].uuid", equalTo("c8a255af-208d-4a98-bbff-8244a7a28609"))
            .body("customers[0].name", equalTo("Nicola"))
            .body("customers[0].surname", equalTo("Landolfi"))
            .body("customers[0].fiscal_code", equalTo("XFFTPK41D24B969W"))
            .body("customers[0].address.street", equalTo("Via fasulla 10"))
            .body("customers[0].address.city", equalTo("Padova"))
            .body("customers[0].address.province", equalTo("Padova"))
            .body("customers[0].address.region", equalTo("Veneto"));
    }

    @Test
    void shouldNotRetrieveAnyCustomer_WhenANonExistingCustomerIsRequested(){
        when()
            .get("/bb1b3c73-cecc-4813-9e45-a34f68c624a8")
        .then()
            .statusCode(200)
            .body("customers", is(empty()));
    }
    
    @Test
    void shouldNotPerformAnyUpdateAndReturnNotFound_WhenTryingToUpdateANonExistingCustomer(){
        // Arrange
        UUID nonExistingCustomerUuid = UUID.fromString("bb1b3c73-cecc-4813-9e45-a34f68c624a8");
        AddressDto givenAddress = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto givenCustomer = new CustomerDto(nonExistingCustomerUuid, "Non", "Esiste", "XFFTPK41D24B969W",
                givenAddress);

        // Act and Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(givenCustomer)
        .when()
            .put("/" + nonExistingCustomerUuid)
        .then()
            .statusCode(404);

        // Make sure that the initial state of the repository hasn't been changed
        assertThat(customerRepository.findAll()).isEmpty();
    }

    @Test
    void shouldRetrieveTheUpdatedCustomerSuccessfully_WhenUpdatingACustomerByAddress(){
        // Arrange
        UUID customerToUpdateUuid = UUID.fromString("c8a255af-208d-4a98-bbff-8244a7a28609");
        AddressDto givenAddress = new AddressDto("Via fasulla 10", "Padova", "Padova", "Veneto");
        CustomerDto givenCustomer = new CustomerDto(customerToUpdateUuid, "Nicola", "Landolfi", "XFFTPK41D24B969W",
                givenAddress);
        customerRepository.save(givenCustomer);

        AddressDto expectedAddress = new AddressDto("Via molto fasulla 20", "Milano", "Milano", "Lombardia");
        CustomerDto expectedCustomer = new CustomerDto(customerToUpdateUuid, givenCustomer.name(),
                givenCustomer.surname(), givenCustomer.fiscalCode(), expectedAddress);

        // Act and Assert
        CustomerDto actualCustomer =
                given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(expectedCustomer)
                .when()
                    .put("/" + customerToUpdateUuid)
                .then()
                    .statusCode(200)
                    .extract().body().as(CustomerDto.class);

        // Make sure that the updated customer, returned within the PUT response,
        // is equal to the customer to update that was provided to the PUT request
        assertThat(actualCustomer).isEqualTo(expectedCustomer);
        // Make sure that the updated customer is stored successfully in the repository
        Optional<CustomerDto> storedCustomer = customerRepository.findByUuid(customerToUpdateUuid.toString());
        assertThat(storedCustomer).get().isEqualTo(expectedCustomer);
    }
}
