package io.landolfi.customer;

import io.landolfi.customer.repository.CustomerRepository;
import io.landolfi.device.DeviceDto;
import io.landolfi.device.repository.DeviceRepository;
import io.landolfi.generator.UniqueIdGenerator;
import io.landolfi.util.rest.ErrorDto;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/customers")
public class CustomerResource {
    private final CustomerRepository<CustomerDto> customerRepository;
    private final UniqueIdGenerator<UUID> idGenerator;
    private final DeviceRepository<DeviceDto> deviceRepository;

    public CustomerResource(CustomerRepository<CustomerDto> customerRepository, UniqueIdGenerator<UUID> idGenerator,
                            DeviceRepository<DeviceDto> deviceRepository) {
        this.customerRepository = customerRepository;
        this.idGenerator = idGenerator;
        this.deviceRepository = deviceRepository;
    }

    @POST
    public Response createCustomer(@Valid CustomerDto received) {
        Optional<UUID> generated = idGenerator.next();
        if (generated.isEmpty()) {
            return Response.serverError().build();
        }

        CustomerDto toReturn = new CustomerDto(generated.get(), received.name(), received.surname(),
                received.fiscalCode(), received.address());
        customerRepository.save(toReturn);
        return Response.created(URI.create("/customers/" + generated.get())).entity(toReturn).build();
    }

    @GET
    public CustomersDto retrieveAllCustomers() {
        return new CustomersDto(customerRepository.findAll());
    }

    @GET
    @Path("/{customerId}")
    public CustomersDto retrieveCustomerById(@PathParam("customerId") String customerId) {
        Optional<CustomerDto> toReturn = customerRepository.findByUuid(customerId);
        if (toReturn.isEmpty()) {
            return new CustomersDto(Collections.emptyList());
        }
        return new CustomersDto(Collections.singletonList(toReturn.get()));
    }

    @PUT
    @Path("/{customerId}")
    public Response updateCustomer(@PathParam("customerId") String customerId, @Valid CustomerDto received) {
        Optional<CustomerDto> toUpdate = customerRepository.findByUuid(customerId);
        if (toUpdate.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!toUpdate.get().isAnyImmutableFieldsDifferentFrom(received)) {
            customerRepository.save(received);
            return Response.ok(received).build();
        }

        // If we got here it means that the client is trying to update one of the immutable fields
        String errorReason = "A customer cannot be updated by the following field(s): uuid, name, surname, " +
                "fiscal_code";
        return Response.status(422).entity(new ErrorDto(errorReason)).build();
    }

    @DELETE
    @Path("/{customerId}")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {
        customerRepository.deleteById(customerId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{customerId}/devices")
    public Response createDeviceAssociatedToCustomer(@PathParam("customerId") String customerId,
                                                     @Valid DeviceDto received) {
        Optional<CustomerDto> optToCreateADeviceFor = customerRepository.findByUuid(customerId);
        if (optToCreateADeviceFor.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        CustomerDto toCreateADeviceFor = optToCreateADeviceFor.get();
        if (toCreateADeviceFor.hasReachedTheMaximumNumberOfAssociatedDevices()) {
            String errorReason = "Cannot create a new device for the given customer because the maximum number of " +
                    "associated devices has been reached";
            return Response.status(Response.Status.CONFLICT).entity(new ErrorDto(errorReason)).build();
        }

        deviceRepository.save(received);
        CustomerDto withTheCreatedDevice = new CustomerDto(toCreateADeviceFor.uuid(), toCreateADeviceFor.name(),
                toCreateADeviceFor.surname(), toCreateADeviceFor.fiscalCode(), toCreateADeviceFor.address(),
                List.of(received));
        customerRepository.save(withTheCreatedDevice);

        return Response.created(URI.create("/devices/" + received.uuid())).entity(received).build();
    }
}
