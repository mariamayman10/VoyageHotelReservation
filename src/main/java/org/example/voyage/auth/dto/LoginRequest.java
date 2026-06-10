package org.example.voyage.auth.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "You must provide email")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "You must provide password")
    private String password;
}
