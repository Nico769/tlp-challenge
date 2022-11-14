package io.landolfi.customer;

import io.landolfi.customer.repository.CustomerRepository;
import io.landolfi.generator.UniqueIdGenerator;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Path("/customers")
public class CustomerResource {
    private final CustomerRepository<CustomerDto> customerRepository;
    private final UniqueIdGenerator<UUID> idGenerator;

    public CustomerResource(CustomerRepository<CustomerDto> customerRepository, UniqueIdGenerator<UUID> idGenerator) {
        this.customerRepository = customerRepository;
        this.idGenerator = idGenerator;
    }

    @POST
    public Response createCustomer(CustomerDto received) {
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
}
