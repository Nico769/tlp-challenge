package io.landolfi.customer;

import io.landolfi.generator.UniqueIdGenerator;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

@Path("/customers")
public class CustomerResource {
    private final Map<UUID, CustomerDto> customers = Collections.synchronizedMap(new HashMap<>());
    private final UniqueIdGenerator<UUID> idGenerator;

    public CustomerResource(UniqueIdGenerator<UUID> idGenerator) {
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
        customers.put(toReturn.uuid(), toReturn);
        return Response.created(URI.create("/customers/" + generated.get())).entity(toReturn).build();
    }

    @GET
    public CustomersDto retrieveAllCustomers() {
        return new CustomersDto(customers.values().stream().toList());
    }

    @GET
    @Path("/{customerId}")
    public CustomersDto retrieveCustomerById(@PathParam("customerId") String customerId) {
        CustomerDto toReturn = customers.get(UUID.fromString(customerId));
        if (toReturn == null) {
            return new CustomersDto(Collections.emptyList());
        }
        return new CustomersDto(Collections.singletonList(toReturn));
    }
}
