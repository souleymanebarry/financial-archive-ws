package sync.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import sync.entities.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Données archivables pour un compte bancaire.
 */

@Data
@Builder
public class AccountArchiveDTO {

    @NotNull(message = "accountId must not be null")
    private UUID accountId;

    @NotBlank(message = "rib must not be blank")
    private String rib;

    @NotNull(message = "balance must not be null")
    @Digits(integer = 18, fraction = 2, message = "balance must be a valid monetary amount")
    private BigDecimal balance;

    @NotNull(message = "createdAt must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @NotNull(message = "status must not be null")
    private AccountStatus status;

    @NotBlank(message = "accountType must not be blank")
    private String accountType;

    @PositiveOrZero(message = "overDraft must be positive or zero")
    private BigDecimal overDraft;

    @PositiveOrZero(message = "interestRate must be positive or zero")
    @Digits(integer = 5, fraction = 4, message = "interestRate must be a valid decimal number")
    private BigDecimal interestRate;

    @Valid
    private List<OperationArchiveDTO> operations;
}
