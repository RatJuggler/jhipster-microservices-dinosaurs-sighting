package com.rj.dinosaurs.sighting.web.rest;

import com.rj.dinosaurs.sighting.domain.Sighting;
import com.rj.dinosaurs.sighting.repository.SightingRepository;
import com.rj.dinosaurs.sighting.repository.search.SightingSearchRepository;
import com.rj.dinosaurs.sighting.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.rj.dinosaurs.sighting.domain.Sighting}.
 */
@RestController
@RequestMapping("/api")
public class SightingResource {

    private final Logger log = LoggerFactory.getLogger(SightingResource.class);

    private static final String ENTITY_NAME = "sightingSighting";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SightingRepository sightingRepository;

    private final SightingSearchRepository sightingSearchRepository;

    public SightingResource(SightingRepository sightingRepository, SightingSearchRepository sightingSearchRepository) {
        this.sightingRepository = sightingRepository;
        this.sightingSearchRepository = sightingSearchRepository;
    }

    /**
     * {@code POST  /sightings} : Create a new sighting.
     *
     * @param sighting the sighting to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sighting, or with status {@code 400 (Bad Request)} if the sighting has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sightings")
    public ResponseEntity<Sighting> createSighting(@Valid @RequestBody Sighting sighting) throws URISyntaxException {
        log.debug("REST request to save Sighting : {}", sighting);
        if (sighting.getId() != null) {
            throw new BadRequestAlertException("A new sighting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Sighting result = sightingRepository.save(sighting);
        sightingSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/sightings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sightings} : Updates an existing sighting.
     *
     * @param sighting the sighting to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sighting,
     * or with status {@code 400 (Bad Request)} if the sighting is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sighting couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sightings")
    public ResponseEntity<Sighting> updateSighting(@Valid @RequestBody Sighting sighting) throws URISyntaxException {
        log.debug("REST request to update Sighting : {}", sighting);
        if (sighting.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Sighting result = sightingRepository.save(sighting);
        sightingSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sighting.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /sightings} : get all the sightings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sightings in body.
     */
    @GetMapping("/sightings")
    public ResponseEntity<List<Sighting>> getAllSightings(Pageable pageable) {
        log.debug("REST request to get a page of Sightings");
        Page<Sighting> page = sightingRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sightings/:id} : get the "id" sighting.
     *
     * @param id the id of the sighting to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sighting, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sightings/{id}")
    public ResponseEntity<Sighting> getSighting(@PathVariable String id) {
        log.debug("REST request to get Sighting : {}", id);
        Optional<Sighting> sighting = sightingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sighting);
    }

    /**
     * {@code DELETE  /sightings/:id} : delete the "id" sighting.
     *
     * @param id the id of the sighting to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sightings/{id}")
    public ResponseEntity<Void> deleteSighting(@PathVariable String id) {
        log.debug("REST request to delete Sighting : {}", id);
        sightingRepository.deleteById(id);
        sightingSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/sightings?query=:query} : search for the sighting corresponding
     * to the query.
     *
     * @param query the query of the sighting search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/sightings")
    public ResponseEntity<List<Sighting>> searchSightings(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Sightings for query {}", query);
        Page<Sighting> page = sightingSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
