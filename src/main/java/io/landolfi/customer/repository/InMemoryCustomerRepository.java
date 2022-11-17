package io.landolfi.customer.repository;


import io.landolfi.customer.CustomerDto;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

/**
 * An in-memory customer repository simulating a database instance.
 * Note that, for the sake of time, I'm not going to write any unit tests for this class
 * even though I would in a production-ready scenario
 */
@ApplicationScoped
public class InMemoryCustomerRepository implements CustomerRepository<CustomerDto> {
    private final Map<UUID, CustomerDto> customers = Collections.synchronizedMap(new HashMap<>());

    @Override
    public List<CustomerDto> findAll() {
        return customers.values().stream().toList();
    }

    @Override
    public Optional<CustomerDto> findByUuid(String uuid) {
        CustomerDto found = customers.get(UUID.fromString(uuid));
        if (found == null) {
            return Optional.empty();
        }
        return Optional.of(found);
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        customers.put(customerDto.uuid(), customerDto);
        return customerDto;
    }

    @Override
    public void deleteAll() {
        customers.clear();
    }

    @Override
    public void deleteById(String uuid) {
        customers.remove(UUID.fromString(uuid));
    }

}
