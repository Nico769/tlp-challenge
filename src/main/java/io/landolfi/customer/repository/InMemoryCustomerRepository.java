package io.landolfi.customer.repository;


import io.landolfi.customer.CustomerDto;
import io.landolfi.device.DeviceDto;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

/**
 * An in-memory customer repository simulating a database instance.
 * Note that, for the sake of time, I'm not going to write any unit tests for this class
 * even though I would in a production-ready scenario
 */
@ApplicationScoped
public class InMemoryCustomerRepository implements CustomerRepository<CustomerDto> {
    private final Map<UUID, CustomerDto> customers = Collections.synchronizedMap(new LinkedHashMap<>());

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

    @Override
    public Optional<CustomerDto> findByDeviceUuid(String deviceUuid) {
        var allCustomers = customers.values().stream();
        var allCustomersDevices = allCustomers.map(CustomerDto::devices);
        var allDevicesFlatten = allCustomersDevices.flatMap(List::stream);
        Optional<DeviceDto> optFoundDevice =
                allDevicesFlatten.filter(device -> device.uuid().equals(deviceUuid)).findFirst();
        if (optFoundDevice.isEmpty()) {
            // No customer has been associated with the given device so far
            return Optional.empty();
        }

        // One customer has been associated with the given device. Now we need to find who is that customer
        DeviceDto foundDevice = optFoundDevice.get();
        Optional<CustomerDto> optMatchingCustomer =
                customers.values().stream().filter(customer -> customer.devices().contains(foundDevice)).findFirst();

        if (optMatchingCustomer.isEmpty()) {
            // TODO Nico769 22/11/22: throw some kind of "Defect" exception since if we hit this branch it means
            //                        there is a programmer mistake somewhere
            return Optional.empty();
        }

        return optMatchingCustomer;
    }
}
