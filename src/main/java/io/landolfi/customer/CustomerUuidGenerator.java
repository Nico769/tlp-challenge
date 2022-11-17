package io.landolfi.customer;

import io.landolfi.generator.UniqueIdGenerator;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CustomerUuidGenerator implements UniqueIdGenerator<UUID> {
    @Override
    public Optional<UUID> next() {
        return Optional.of(UUID.randomUUID());
    }
}
