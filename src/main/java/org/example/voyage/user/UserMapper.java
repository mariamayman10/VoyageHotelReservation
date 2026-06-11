package org.example.voyage.user;

import org.example.voyage.user.dto.ManagerSummaryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public ManagerSummaryResponse toManagerSummaryResponse(User user);
}
