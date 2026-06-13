package org.example.voyage.hotel.dto;

import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {
    @Nullable
    private String query;
    @Nullable
    private String country;
    @Nullable
    private String city;
    @Nullable
    private Set<UUID> amenityIds;
    private int page = 0;
    private int size = 10;
    private String sortBy = "name";
    @Enumerated(EnumType.STRING)
    private SortDirection sortDirection = SortDirection.ASC;
    public enum SortDirection {
        ASC, DESC;
    }
}
