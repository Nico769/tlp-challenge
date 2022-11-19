package io.landolfi.device;

import io.landolfi.device.repository.DeviceRepository;
import io.landolfi.util.rest.ErrorDto;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;

@Path("/devices")
public class DeviceResource {

    private final DeviceRepository<DeviceDto> deviceRepository;

    public DeviceResource(DeviceRepository<DeviceDto> deviceRepository) {
        this.deviceRepository = deviceRepository;
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
           return new DevicesDto(Collections.emptyList());
       }
       return new DevicesDto(Collections.singletonList(toReturn.get()));
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
            return Response.ok(received).build();
        }

        // If we got here it means that the client is trying to update one of the immutable fields
        String errorReason = "A device cannot be updated by the following field(s): uuid";
        return Response.status(422).entity(new ErrorDto(errorReason)).build();
    }
}
