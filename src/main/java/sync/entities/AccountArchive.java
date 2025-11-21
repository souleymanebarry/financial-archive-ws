package sync.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;
import sync.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "account_archive")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID archiveAccountId;

    private UUID originalAccountId;

    private String rib;

    private BigDecimal balance;

    private LocalDateTime createdAt;

    private AccountStatus status;

    private String accountType;

    private BigDecimal overDraft;

    private BigDecimal interestRate;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ARCHIVE_ID")
    @ToString.Exclude
    private CustomerArchive customerArchive;

    @Builder.Default
    @OneToMany(mappedBy = "accountArchive", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OperationArchive> operations = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AccountArchive that = (AccountArchive) o;
        return archiveAccountId != null && Objects.equals(archiveAccountId, that.archiveAccountId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
