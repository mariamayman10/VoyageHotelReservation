package org.example.voyage.hotel;

import jakarta.persistence.criteria.Join;
import org.example.voyage.amenity.Amenity;
import org.example.voyage.user.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;

public class HotelSpecification {
    public static Specification<Hotel> byManager(User manager) {
        return (root, q, cb) -> cb.equal(root.get("manager"), manager);
    }

    public static Specification<Hotel> byQuery(String query) {
        String pattern = "%" + query.toLowerCase() + "%";
        return ((root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
        ));
    }

    public static Specification<Hotel> byCountry(String country){
        String pattern = "%" + country.toLowerCase() + "%";
        return (root, q, cb) -> cb.like(
                cb.lower(root.get("country")), pattern
        );
    }

    public static Specification<Hotel> byCity(String city){
        String pattern = "%" + city.toLowerCase() + "%";
        return (root, q, cb) -> cb.like(
                cb.lower(root.get("city")), pattern
        );
    }

    public static Specification<Hotel> byAmenityIds(Set<UUID> amenityIds){
        return (root, q, cb) -> {
            Join<Hotel, Amenity> amenities = root.join("amenities");
            return amenities.get("id").in(amenityIds);
        };
    }
}
