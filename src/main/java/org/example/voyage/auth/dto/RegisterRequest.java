package org.example.voyage.auth.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "You must provide name")
    private String name;
    @NotBlank(message = "You must provide email")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "You must provide password")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Weak password, password must be at least 8 characters and contain letters and numbers"
    )
    private String password;
    @NotBlank(message = "You must provide phone number")
    @Pattern(
            regexp = "^01[0-2,5]{1}[0-9]{8}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;
}
