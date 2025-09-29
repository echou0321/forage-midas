package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserRecord, Long> {
    UserRecord findByName(String name);
    Optional<UserRecord> findById(Long id);

}
