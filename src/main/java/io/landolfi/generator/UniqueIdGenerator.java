package io.landolfi.generator;

import java.util.Optional;

public interface UniqueIdGenerator<T> {
    Optional<T> next();
}
