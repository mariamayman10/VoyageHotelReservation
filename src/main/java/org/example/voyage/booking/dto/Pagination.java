package org.example.voyage.booking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagination {
    private int page = 0;
    private int size = 10;
}
