package io.landolfi.device;

import io.landolfi.device.repository.DeviceRepository;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
}
