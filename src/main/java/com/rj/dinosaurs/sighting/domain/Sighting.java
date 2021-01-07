package com.rj.dinosaurs.sighting.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.Instant;

import com.rj.dinosaurs.sighting.domain.enumeration.Heading;

/**
 * A Sighting.
 */
@Document(collection = "sighting")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "sighting")
public class Sighting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Min(value = 0L)
    @Field("dinosaur")
    private Long dinosaur;

    @NotNull
    @Min(value = 0L)
    @Field("by_user")
    private Long byUser;

    @NotNull
    @Field("when_dt")
    private Instant whenDt;

    @NotNull
    @Field("lat")
    private Float lat;

    @NotNull
    @Field("lng")
    private Float lng;

    @Min(value = 0)
    @Max(value = 999)
    @Field("number")
    private Integer number;

    @Field("heading")
    private Heading heading;

    @Size(max = 64)
    @Field("notes")
    private String notes;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDinosaur() {
        return dinosaur;
    }

    public Sighting dinosaur(Long dinosaur) {
        this.dinosaur = dinosaur;
        return this;
    }

    public void setDinosaur(Long dinosaur) {
        this.dinosaur = dinosaur;
    }

    public Long getByUser() {
        return byUser;
    }

    public Sighting byUser(Long byUser) {
        this.byUser = byUser;
        return this;
    }

    public void setByUser(Long byUser) {
        this.byUser = byUser;
    }

    public Instant getWhenDt() {
        return whenDt;
    }

    public Sighting whenDt(Instant whenDt) {
        this.whenDt = whenDt;
        return this;
    }

    public void setWhenDt(Instant whenDt) {
        this.whenDt = whenDt;
    }

    public Float getLat() {
        return lat;
    }

    public Sighting lat(Float lat) {
        this.lat = lat;
        return this;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public Sighting lng(Float lng) {
        this.lng = lng;
        return this;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Integer getNumber() {
        return number;
    }

    public Sighting number(Integer number) {
        this.number = number;
        return this;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Heading getHeading() {
        return heading;
    }

    public Sighting heading(Heading heading) {
        this.heading = heading;
        return this;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public String getNotes() {
        return notes;
    }

    public Sighting notes(String notes) {
        this.notes = notes;
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sighting)) {
            return false;
        }
        return id != null && id.equals(((Sighting) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sighting{" +
            "id=" + getId() +
            ", dinosaur=" + getDinosaur() +
            ", byUser=" + getByUser() +
            ", whenDt='" + getWhenDt() + "'" +
            ", lat=" + getLat() +
            ", lng=" + getLng() +
            ", number=" + getNumber() +
            ", heading='" + getHeading() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
