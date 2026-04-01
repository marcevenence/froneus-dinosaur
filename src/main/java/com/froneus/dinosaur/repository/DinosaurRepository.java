package com.froneus.dinosaur.repository;

import com.froneus.dinosaur.model.Dinosaur;
import com.froneus.dinosaur.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DinosaurRepository extends MongoRepository<Dinosaur, String> {
    Optional<Dinosaur> findByName(String name);
    boolean existsByName(String name);
    boolean existsById(String id);

    List<Dinosaur> findByStatusAndExtinctionDateBefore(Status status, LocalDateTime extinctionDate);
    List<Dinosaur> findByExtinctionDateBeforeAndStatusNot(LocalDateTime extinctionDate, Status status);
}
