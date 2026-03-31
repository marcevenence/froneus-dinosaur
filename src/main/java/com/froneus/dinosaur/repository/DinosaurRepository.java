package com.froneus.dinosaur.repository;

import com.froneus.dinosaur.model.Dinosaur;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DinosaurRepository extends MongoRepository<Dinosaur, String> {
    Optional<Dinosaur> findByName(String name);
    boolean existsByName(String name);
}
