package com.rj.dinosaurs.sighting.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link SightingSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class SightingSearchRepositoryMockConfiguration {

    @MockBean
    private SightingSearchRepository mockSightingSearchRepository;

}
