package io.landolfi.device;

import io.landolfi.customer.CustomerDto;
import io.landolfi.customer.repository.CustomerRepository;
import io.landolfi.device.repository.DeviceRepository;
import io.landolfi.util.rest.ErrorDto;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Path("/devices")
public class DeviceResource {

    private final DeviceRepository<DeviceDto> deviceRepository;
    private final CustomerRepository<CustomerDto> customerRepository;

    public DeviceResource(DeviceRepository<DeviceDto> deviceRepository,
                          CustomerRepository<CustomerDto> customerRepository) {
        this.deviceRepository = deviceRepository;
        this.customerRepository = customerRepository;
    }

    @POST
    public Response createDevice(@Valid DeviceDto received) {
        deviceRepository.save(received);
        return Response.created(URI.create("/devices/" + received.uuid())).entity(received).build();
    }

    @GET
    public DevicesDto retrieveAllDevices() {
        return new DevicesDto(deviceRepository.findAll());
    }

    @GET
    @Path("/{deviceId}")
    public DevicesDto retrieveDeviceById(@PathParam("deviceId") String deviceId) {
       Optional<DeviceDto> toReturn = deviceRepository.findByUuid(deviceId);
       if (toReturn.isEmpty()) {
           return DevicesDto.empty();
       }
       return DevicesDto.withOneDevice(toReturn.get());
    }

    @PUT
    @Path("/{deviceId}")
    public Response updateDevice(@PathParam("deviceId") String deviceId, @Valid DeviceDto received) {
        Optional<DeviceDto> toUpdate = deviceRepository.findByUuid(deviceId);
        if (toUpdate.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!toUpdate.get().isAnyImmutableFieldDifferentFrom(received)) {
            deviceRepository.save(received);
            Optional<CustomerDto> optAssociatedCustomer = customerRepository.findByDeviceUuid(deviceId);
            if (optAssociatedCustomer.isEmpty()) {
                // The given device has not been associated to any customer yet
                return Response.ok(received).build();
            }
            // The given device is associated to a customer, so we need to reflect this update onto the customer
            CustomerDto toUpdateCustomer = optAssociatedCustomer.get();

            // Remove the device to update from the customer's devices
            List<DeviceDto> toUpdateDevices = new ArrayList<>(toUpdateCustomer.devices());
            toUpdateDevices.removeIf(d -> d.uuid().equals(deviceId));

            // Add the updated device to the customer
            CustomerDto updated = new CustomerDto(toUpdateCustomer.uuid(), toUpdateCustomer.name(),
                    toUpdateCustomer.surname(), toUpdateCustomer.fiscalCode(), toUpdateCustomer.address(),
                    Stream.concat(toUpdateDevices.stream(), Stream.of(received)).toList());
            customerRepository.save(updated);

            return Response.ok(received).build();
        }

        // If we got here it means that the client is trying to update one of the immutable fields
        String errorReason = "A device cannot be updated by the following field(s): uuid";
        return Response.status(422).entity(new ErrorDto(errorReason)).build();
    }

    @DELETE
    @Path("/{deviceId}")
    public Response deleteDevice(@PathParam("deviceId") String deviceId) {
        deviceRepository.deleteById(deviceId);

        Optional<CustomerDto> optAssociatedCustomer = customerRepository.findByDeviceUuid(deviceId);
        if (optAssociatedCustomer.isEmpty()) {
            // Nothing else to do because the given device is not associated to any customer
            return Response.noContent().build();
        }

        CustomerDto toUpdateCustomer = optAssociatedCustomer.get();

        List<DeviceDto> toUpdateDevices = new ArrayList<>(toUpdateCustomer.devices());
        toUpdateDevices.removeIf(d -> d.uuid().equals(deviceId));

        CustomerDto updated = new CustomerDto(toUpdateCustomer.uuid(), toUpdateCustomer.name(), toUpdateCustomer.surname(),
                toUpdateCustomer.fiscalCode(),
                toUpdateCustomer.address(),toUpdateDevices);
        customerRepository.save(updated);

        return Response.noContent().build();
    }
}
