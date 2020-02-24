package com.rj.dinosaurs.sighting.repository.search;

import com.rj.dinosaurs.sighting.domain.Sighting;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Sighting} entity.
 */
public interface SightingSearchRepository extends ElasticsearchRepository<Sighting, String> {
}
