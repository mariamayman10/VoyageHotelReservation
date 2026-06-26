package org.example.voyage.user;

import org.apache.catalina.Manager;
import org.example.voyage.user.dto.ManagerSummaryResponse;
import org.example.voyage.user.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ManagerSummaryResponse toManagerSummaryResponse(User user);
    UserResponse toUserResponse(User user);
}
