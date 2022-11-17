package io.landolfi.doubles;

import io.landolfi.generator.UniqueIdGenerator;
import io.quarkus.test.Mock;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

@Mock
@ApplicationScoped
public class CustomerUuidGeneratorFake implements UniqueIdGenerator<UUID> {
    private static final UUID firstCustomerUuid = UUID.fromString("c8a255af-208d-4a98-bbff-8244a7a28609");
    private static final UUID secondCustomerUuid = UUID.fromString("12014578-3bd6-4fd8-9dc2-2e40f83831d2");
    private final LinkedBlockingQueue<UUID> q = new LinkedBlockingQueue<>();

    public CustomerUuidGeneratorFake() {
        reset();
    }

    @Override
    public Optional<UUID> next() {
        try {
            UUID next = q.take();
            return Optional.of(next);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    public void reset() {
        try {
            q.clear();
            q.put(firstCustomerUuid);
            q.put(secondCustomerUuid);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
