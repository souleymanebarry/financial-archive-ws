package sync.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import sync.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Données archivables pour une opération (débit/crédit).
 */

@Data
@Builder
public class OperationArchiveDTO {

    @NotNull(message = "operationId must not be null")
    private UUID operationId;

    @NotBlank(message = "operationNumber must not be blank")
    private String operationNumber;

    @Positive(message = "operationAmount must be positive")
    private BigDecimal operationAmount;

    @NotNull(message = "operationDate must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime operationDate;

    @NotNull(message = "operationType must not be null")
    private OperationType operationType;

    @NotBlank(message = "description must not be blank")
    private String description;
}
