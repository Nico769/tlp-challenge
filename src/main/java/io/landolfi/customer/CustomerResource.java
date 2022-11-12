package io.landolfi.customer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Path("/customers")
public class CustomerResource {
    private final Map<UUID, CustomerDto> customers = Collections.synchronizedMap(new HashMap<>());

    @POST
    public CustomerDto createCustomer(CustomerDto received) {
        CustomerDto toReturn = new CustomerDto(UUID.fromString("c8a255af-208d-4a98-bbff-8244a7a28609"),
                received.name(), received.surname(), received.fiscalCode(), received.address());
        customers.put(toReturn.uuid(), toReturn);
        return toReturn;
    }
}
