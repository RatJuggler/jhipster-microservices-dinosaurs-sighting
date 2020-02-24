package com.rj.dinosaurs.sighting.repository;

import com.rj.dinosaurs.sighting.domain.Sighting;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Sighting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SightingRepository extends MongoRepository<Sighting, String> {

}
