package org.example.safebox.repository;

import org.example.safebox.model.Safebox;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SafeboxRepository extends CrudRepository<Safebox, Long> {

    Optional<Safebox> findByName(String name);

}
