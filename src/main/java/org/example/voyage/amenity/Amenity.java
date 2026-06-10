package org.example.voyage.amenity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.example.voyage.hotel.Hotel;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "amenities")
public class Amenity {
    @Id
    @UuidGenerator
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String icon;
    @ManyToMany(mappedBy = "amenities")
    private Set<Hotel> hotels = new HashSet<>();
}
