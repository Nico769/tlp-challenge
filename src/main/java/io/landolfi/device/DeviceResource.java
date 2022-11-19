package io.landolfi.device;

import io.landolfi.device.repository.DeviceRepository;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;

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
}
