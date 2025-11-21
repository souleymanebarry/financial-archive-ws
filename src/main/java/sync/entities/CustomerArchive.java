package sync.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sync.enums.Gender;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer_archive")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID archiveCustomerId;

    private UUID originalCustomerId;

    private String firstName;

    private String lastName;

    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime archiveCreatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "customerArchive", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountArchive> accounts = new ArrayList<>();

}

