package io.landolfi.customer.repository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository<T> {
    List<T> findAll();

    Optional<T> findByUuid(String uuid);

    T save(T customer);

    void deleteAll();

    void deleteById(String uuid);
}
