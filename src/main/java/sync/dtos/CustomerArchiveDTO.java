package sync.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sync.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Représente les données archivables d’un client (Customer)
 * envoyées par le microservice financial-service.
 */

@Data
@Builder
public class CustomerArchiveDTO {

    @NotNull(message = "customerId must not be null")
    private UUID customerId;

    @NotBlank(message = "firstName must not be blank")
    private String firstName;

    @NotBlank(message = "lastName must not be blank")
    private String lastName;

    @Email
    @NotBlank(message = "email must not be blank")
    private String email;

    @NotNull(message = "gender must not be null")
    private Gender gender;

    private List<AccountArchiveDTO> accounts;
}
