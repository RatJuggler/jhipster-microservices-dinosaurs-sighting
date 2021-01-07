package com.rj.dinosaurs.sighting.web.rest;

import com.rj.dinosaurs.sighting.SightingApp;
import com.rj.dinosaurs.sighting.domain.Sighting;
import com.rj.dinosaurs.sighting.repository.SightingRepository;
import com.rj.dinosaurs.sighting.repository.search.SightingSearchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rj.dinosaurs.sighting.domain.enumeration.Heading;
/**
 * Integration tests for the {@link SightingResource} REST controller.
 */
@SpringBootTest(classes = SightingApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class SightingResourceIT {

    private static final Long DEFAULT_DINOSAUR = 0L;
    private static final Long UPDATED_DINOSAUR = 1L;

    private static final Long DEFAULT_BY_USER = 0L;
    private static final Long UPDATED_BY_USER = 1L;

    private static final Instant DEFAULT_WHEN_DT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_WHEN_DT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Float DEFAULT_LAT = 1F;
    private static final Float UPDATED_LAT = 2F;

    private static final Float DEFAULT_LNG = 1F;
    private static final Float UPDATED_LNG = 2F;

    private static final Integer DEFAULT_NUMBER = 0;
    private static final Integer UPDATED_NUMBER = 1;

    private static final Heading DEFAULT_HEADING = Heading.STATIONARY;
    private static final Heading UPDATED_HEADING = Heading.NORTH;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    @Autowired
    private SightingRepository sightingRepository;

    /**
     * This repository is mocked in the com.rj.dinosaurs.sighting.repository.search test package.
     *
     * @see com.rj.dinosaurs.sighting.repository.search.SightingSearchRepositoryMockConfiguration
     */
    @Autowired
    private SightingSearchRepository mockSightingSearchRepository;

    @Autowired
    private MockMvc restSightingMockMvc;

    private Sighting sighting;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sighting createEntity() {
        Sighting sighting = new Sighting()
            .dinosaur(DEFAULT_DINOSAUR)
            .byUser(DEFAULT_BY_USER)
            .whenDt(DEFAULT_WHEN_DT)
            .lat(DEFAULT_LAT)
            .lng(DEFAULT_LNG)
            .number(DEFAULT_NUMBER)
            .heading(DEFAULT_HEADING)
            .notes(DEFAULT_NOTES);
        return sighting;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sighting createUpdatedEntity() {
        Sighting sighting = new Sighting()
            .dinosaur(UPDATED_DINOSAUR)
            .byUser(UPDATED_BY_USER)
            .whenDt(UPDATED_WHEN_DT)
            .lat(UPDATED_LAT)
            .lng(UPDATED_LNG)
            .number(UPDATED_NUMBER)
            .heading(UPDATED_HEADING)
            .notes(UPDATED_NOTES);
        return sighting;
    }

    @BeforeEach
    public void initTest() {
        sightingRepository.deleteAll();
        sighting = createEntity();
    }

    @Test
    public void createSighting() throws Exception {
        int databaseSizeBeforeCreate = sightingRepository.findAll().size();
        // Create the Sighting
        restSightingMockMvc.perform(post("/api/sightings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sighting)))
            .andExpect(status().isCreated());

        // Validate the Sighting in the database
        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeCreate + 1);
        Sighting testSighting = sightingList.get(sightingList.size() - 1);
        assertThat(testSighting.getDinosaur()).isEqualTo(DEFAULT_DINOSAUR);
        assertThat(testSighting.getByUser()).isEqualTo(DEFAULT_BY_USER);
        assertThat(testSighting.getWhenDt()).isEqualTo(DEFAULT_WHEN_DT);
        assertThat(testSighting.getLat()).isEqualTo(DEFAULT_LAT);
        assertThat(testSighting.getLng()).isEqualTo(DEFAULT_LNG);
        assertThat(testSighting.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testSighting.getHeading()).isEqualTo(DEFAULT_HEADING);
        assertThat(testSighting.getNotes()).isEqualTo(DEFAULT_NOTES);

        // Validate the Sighting in Elasticsearch
        verify(mockSightingSearchRepository, times(1)).save(testSighting);
    }

    @Test
    public void createSightingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sightingRepository.findAll().size();

        // Create the Sighting with an existing ID
        sighting.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restSightingMockMvc.perform(post("/api/sightings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sighting)))
            .andExpect(status().isBadRequest());

        // Validate the Sighting in the database
        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeCreate);

        // Validate the Sighting in Elasticsearch
        verify(mockSightingSearchRepository, times(0)).save(sighting);
    }


    @Test
    public void checkDinosaurIsRequired() throws Exception {
        int databaseSizeBeforeTest = sightingRepository.findAll().size();
        // set the field null
        sighting.setDinosaur(null);

        // Create the Sighting, which fails.


        restSightingMockMvc.perform(post("/api/sightings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sighting)))
            .andExpect(status().isBadRequest());

        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkByUserIsRequired() throws Exception {
        int databaseSizeBeforeTest = sightingRepository.findAll().size();
        // set the field null
        sighting.setByUser(null);

        // Create the Sighting, which fails.


        restSightingMockMvc.perform(post("/api/sightings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sighting)))
            .andExpect(status().isBadRequest());

        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkWhenDtIsRequired() throws Exception {
        int databaseSizeBeforeTest = sightingRepository.findAll().size();
        // set the field null
        sighting.setWhenDt(null);

        // Create the Sighting, which fails.


        restSightingMockMvc.perform(post("/api/sightings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sighting)))
            .andExpect(status().isBadRequest());

        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkLatIsRequired() throws Exception {
        int databaseSizeBeforeTest = sightingRepository.findAll().size();
        // set the field null
        sighting.setLat(null);

        // Create the Sighting, which fails.


        restSightingMockMvc.perform(post("/api/sightings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sighting)))
            .andExpect(status().isBadRequest());

        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkLngIsRequired() throws Exception {
        int databaseSizeBeforeTest = sightingRepository.findAll().size();
        // set the field null
        sighting.setLng(null);

        // Create the Sighting, which fails.


        restSightingMockMvc.perform(post("/api/sightings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sighting)))
            .andExpect(status().isBadRequest());

        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllSightings() throws Exception {
        // Initialize the database
        sightingRepository.save(sighting);

        // Get all the sightingList
        restSightingMockMvc.perform(get("/api/sightings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sighting.getId())))
            .andExpect(jsonPath("$.[*].dinosaur").value(hasItem(DEFAULT_DINOSAUR.intValue())))
            .andExpect(jsonPath("$.[*].byUser").value(hasItem(DEFAULT_BY_USER.intValue())))
            .andExpect(jsonPath("$.[*].whenDt").value(hasItem(DEFAULT_WHEN_DT.toString())))
            .andExpect(jsonPath("$.[*].lat").value(hasItem(DEFAULT_LAT.doubleValue())))
            .andExpect(jsonPath("$.[*].lng").value(hasItem(DEFAULT_LNG.doubleValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].heading").value(hasItem(DEFAULT_HEADING.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }
    
    @Test
    public void getSighting() throws Exception {
        // Initialize the database
        sightingRepository.save(sighting);

        // Get the sighting
        restSightingMockMvc.perform(get("/api/sightings/{id}", sighting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sighting.getId()))
            .andExpect(jsonPath("$.dinosaur").value(DEFAULT_DINOSAUR.intValue()))
            .andExpect(jsonPath("$.byUser").value(DEFAULT_BY_USER.intValue()))
            .andExpect(jsonPath("$.whenDt").value(DEFAULT_WHEN_DT.toString()))
            .andExpect(jsonPath("$.lat").value(DEFAULT_LAT.doubleValue()))
            .andExpect(jsonPath("$.lng").value(DEFAULT_LNG.doubleValue()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.heading").value(DEFAULT_HEADING.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }
    @Test
    public void getNonExistingSighting() throws Exception {
        // Get the sighting
        restSightingMockMvc.perform(get("/api/sightings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateSighting() throws Exception {
        // Initialize the database
        sightingRepository.save(sighting);

        int databaseSizeBeforeUpdate = sightingRepository.findAll().size();

        // Update the sighting
        Sighting updatedSighting = sightingRepository.findById(sighting.getId()).get();
        updatedSighting
            .dinosaur(UPDATED_DINOSAUR)
            .byUser(UPDATED_BY_USER)
            .whenDt(UPDATED_WHEN_DT)
            .lat(UPDATED_LAT)
            .lng(UPDATED_LNG)
            .number(UPDATED_NUMBER)
            .heading(UPDATED_HEADING)
            .notes(UPDATED_NOTES);

        restSightingMockMvc.perform(put("/api/sightings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedSighting)))
            .andExpect(status().isOk());

        // Validate the Sighting in the database
        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeUpdate);
        Sighting testSighting = sightingList.get(sightingList.size() - 1);
        assertThat(testSighting.getDinosaur()).isEqualTo(UPDATED_DINOSAUR);
        assertThat(testSighting.getByUser()).isEqualTo(UPDATED_BY_USER);
        assertThat(testSighting.getWhenDt()).isEqualTo(UPDATED_WHEN_DT);
        assertThat(testSighting.getLat()).isEqualTo(UPDATED_LAT);
        assertThat(testSighting.getLng()).isEqualTo(UPDATED_LNG);
        assertThat(testSighting.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testSighting.getHeading()).isEqualTo(UPDATED_HEADING);
        assertThat(testSighting.getNotes()).isEqualTo(UPDATED_NOTES);

        // Validate the Sighting in Elasticsearch
        verify(mockSightingSearchRepository, times(1)).save(testSighting);
    }

    @Test
    public void updateNonExistingSighting() throws Exception {
        int databaseSizeBeforeUpdate = sightingRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSightingMockMvc.perform(put("/api/sightings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(sighting)))
            .andExpect(status().isBadRequest());

        // Validate the Sighting in the database
        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sighting in Elasticsearch
        verify(mockSightingSearchRepository, times(0)).save(sighting);
    }

    @Test
    public void deleteSighting() throws Exception {
        // Initialize the database
        sightingRepository.save(sighting);

        int databaseSizeBeforeDelete = sightingRepository.findAll().size();

        // Delete the sighting
        restSightingMockMvc.perform(delete("/api/sightings/{id}", sighting.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sighting> sightingList = sightingRepository.findAll();
        assertThat(sightingList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Sighting in Elasticsearch
        verify(mockSightingSearchRepository, times(1)).deleteById(sighting.getId());
    }

    @Test
    public void searchSighting() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        sightingRepository.save(sighting);
        when(mockSightingSearchRepository.search(queryStringQuery("id:" + sighting.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(sighting), PageRequest.of(0, 1), 1));

        // Search the sighting
        restSightingMockMvc.perform(get("/api/_search/sightings?query=id:" + sighting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sighting.getId())))
            .andExpect(jsonPath("$.[*].dinosaur").value(hasItem(DEFAULT_DINOSAUR.intValue())))
            .andExpect(jsonPath("$.[*].byUser").value(hasItem(DEFAULT_BY_USER.intValue())))
            .andExpect(jsonPath("$.[*].whenDt").value(hasItem(DEFAULT_WHEN_DT.toString())))
            .andExpect(jsonPath("$.[*].lat").value(hasItem(DEFAULT_LAT.doubleValue())))
            .andExpect(jsonPath("$.[*].lng").value(hasItem(DEFAULT_LNG.doubleValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].heading").value(hasItem(DEFAULT_HEADING.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }
}
