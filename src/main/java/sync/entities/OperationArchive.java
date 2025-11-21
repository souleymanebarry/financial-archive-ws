package sync.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;
import sync.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "operation_archive")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID archiveOperationId;

    private UUID originalOperationId;

    private String operationNumber;

    private BigDecimal operationAmount;

    private LocalDateTime operationDate;

    private OperationType operationType;

    private String description;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ARCHIVE_ID")
    @ToString.Exclude
    private AccountArchive accountArchive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OperationArchive that = (OperationArchive) o;
        return archiveOperationId != null && Objects.equals(archiveOperationId, that.archiveOperationId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
