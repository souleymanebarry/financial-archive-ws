package sync.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sync.dtos.AccountArchiveDTO;
import sync.dtos.CustomerArchiveDTO;
import sync.dtos.OperationArchiveDTO;
import sync.entities.AccountArchive;
import sync.entities.CustomerArchive;
import sync.entities.OperationArchive;
import sync.mappers.CustomerArchiveMapper;
import sync.repositories.CustomerArchiveRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static sync.entities.enums.AccountStatus.ACTIVATED;
import static sync.entities.enums.Gender.MALE;
import static sync.entities.enums.OperationType.CREDIT;
import static sync.entities.enums.OperationType.DEBIT;


@ExtendWith(MockitoExtension.class)
class CustomerArchiveServiceImplTest {

    @Mock
    private CustomerArchiveRepository customerArchiveRepository;

    @Mock
    private CustomerArchiveMapper customerArchiveMapper;

    @InjectMocks
    private CustomerArchiveServiceImpl archiveService;

    private CustomerArchiveDTO customerArchiveDTO;

    private final UUID customerId = UUID.randomUUID();

    private List<AccountArchive> accountArchives;

    @BeforeEach
    void setUp() {
        OperationArchiveDTO operation = OperationArchiveDTO.builder()
                .operationId(UUID.randomUUID())
                .operationNumber("OP-20251028-003931")
                .operationAmount(BigDecimal.valueOf(10_000))
                .operationDate(LocalDateTime.now())
                .operationType(CREDIT)
                .description("Versement salaire")
                .build();

        OperationArchiveDTO operation2 = OperationArchiveDTO.builder()
                .operationId(UUID.randomUUID())
                .operationNumber("OP-20251028-240201")
                .operationAmount(BigDecimal.valueOf(30_000))
                .operationDate(LocalDateTime.now())
                .operationType(DEBIT)
                .description("Achat ou retrait")
                .build();

        OperationArchiveDTO operation3 = OperationArchiveDTO.builder()
                .operationId(UUID.randomUUID())
                .operationNumber("OP-20251028-958594")
                .operationAmount(BigDecimal.valueOf(10))
                .operationDate(LocalDateTime.now())
                .operationType(DEBIT)
                .description("Achat ou retrait")
                .build();

        AccountArchiveDTO currentAccount = AccountArchiveDTO.builder()
                .accountId(UUID.randomUUID())
                .rib("FR76 1111 3333 3167 6565 374")
                .balance(BigDecimal.valueOf(23_000))
                .createdAt(LocalDateTime.now())
                .status(ACTIVATED)
                .accountType("CURRENT ACCOUNT")
                .operations(List.of(operation, operation2))
                .overDraft(BigDecimal.valueOf(200))
                .build();

        AccountArchiveDTO savingAccount = AccountArchiveDTO.builder()
                .accountId(UUID.randomUUID())
                .rib("FR76 1111 3333 3167 6565 374")
                .balance(BigDecimal.valueOf(20_000))
                .createdAt(LocalDateTime.now())
                .status(ACTIVATED)
                .accountType("SAVING ACCOUNT")
                .interestRate(BigDecimal.valueOf(2.5))
                .operations(List.of(operation3))
                .build();

        customerArchiveDTO = CustomerArchiveDTO.builder()
                .customerId(customerId)
                .firstName("Alex")
                .lastName("CONDE")
                .email("alex.conde-ext@google.com")
                .accounts(List.of(currentAccount, savingAccount))
                .gender(MALE)
                .build();

        accountArchives = customerArchiveDTO.getAccounts()
                .stream()
                .map(dto -> AccountArchive.builder()
                        .archiveAccountId(UUID.randomUUID())
                        .rib(dto.getRib())
                        .balance(dto.getBalance())
                        .createdAt(dto.getCreatedAt())
                        .status(dto.getStatus())
                        .accountType(dto.getAccountType())
                        .overDraft(dto.getOverDraft())
                        .interestRate(dto.getInterestRate())
                        .operations(dto.getOperations().stream()
                                .map(op -> OperationArchive.builder()
                                        .archiveOperationId(UUID.randomUUID())
                                        .originalOperationId(op.getOperationId())
                                        .operationNumber(op.getOperationNumber())
                                        .operationDate(op.getOperationDate())
                                        .operationAmount(op.getOperationAmount())
                                        .operationType(op.getOperationType())
                                        .description(op.getDescription())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @Test
    void shouldSaveArchivedCustomerWhenAllCustomerArchiveDtoDataIsValid() {
        //Arrange
        CustomerArchive customerArchive = CustomerArchive.builder()
                .archiveCustomerId(UUID.randomUUID())
                .originalCustomerId(customerArchiveDTO.getCustomerId())
                .firstName(customerArchiveDTO.getFirstName())
                .lastName(customerArchiveDTO.getLastName())
                .email(customerArchiveDTO.getEmail())
                .gender(customerArchiveDTO.getGender())
                .archiveCreatedAt(LocalDateTime.now())
                .accounts(accountArchives)
                .build();

        when(customerArchiveMapper.customerArchiveDtoToCustomerArchive(customerArchiveDTO))
                .thenReturn(customerArchive);

        ArgumentCaptor<CustomerArchive> customerArchiveCaptor = ArgumentCaptor.forClass(CustomerArchive.class);

        //Act
        archiveService.saveArchivedCustomer(customerArchiveDTO);

        // verify
        verify(customerArchiveRepository).save(customerArchiveCaptor.capture());
        verifyNoMoreInteractions(customerArchiveRepository);

        // Extract captured argument
        CustomerArchive savedCustomer = customerArchiveCaptor.getValue();

        //Assert
        assertSoftly(softly -> {
            softly.assertThat(savedCustomer.getFirstName()).isEqualTo("Alex");
            softly.assertThat(savedCustomer.getLastName()).isEqualTo("CONDE");
            softly.assertThat(savedCustomer.getEmail()).isEqualTo("alex.conde-ext@google.com");
            softly.assertThat(savedCustomer.getAccounts()).hasSize(2);
            softly.assertThat(savedCustomer.getAccounts().get(0).getBalance()).isEqualByComparingTo(BigDecimal.valueOf(23_000));
            softly.assertThat(savedCustomer.getAccounts().get(1).getBalance()).isEqualByComparingTo(BigDecimal.valueOf(20_000));
            softly.assertThat(savedCustomer.getAccounts().get(0).getAccountType()).isEqualTo("CURRENT ACCOUNT");
            softly.assertThat(savedCustomer.getAccounts().get(1).getAccountType()).isEqualTo("SAVING ACCOUNT");
            softly.assertThat(savedCustomer.getAccounts().get(0).getOperations()).hasSize(2);
            softly.assertThat(savedCustomer.getAccounts().get(1).getOperations()).hasSize(1);
        });
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenMapperReturnsNull() {
        // Arrange
        when(customerArchiveMapper.customerArchiveDtoToCustomerArchive(customerArchiveDTO))
                .thenReturn(null);

        // Act + Assert
        assertThatThrownBy(() -> archiveService.saveArchivedCustomer(customerArchiveDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(customerArchiveDTO.getCustomerId().toString());

        verify(customerArchiveRepository, never()).save(any());
    }
}
