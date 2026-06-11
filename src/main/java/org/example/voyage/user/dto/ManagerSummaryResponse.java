package org.example.voyage.user.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerSummaryResponse {
    private UUID id;
    private String name;
    private String email;
}
