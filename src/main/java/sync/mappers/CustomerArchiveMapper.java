package sync.mappers;


import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sync.dtos.AccountArchiveDTO;
import sync.dtos.CustomerArchiveDTO;
import sync.dtos.OperationArchiveDTO;
import sync.entities.AccountArchive;
import sync.entities.CustomerArchive;
import sync.entities.OperationArchive;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerArchiveMapper {

    // ===== CUSTOMER =====
    @Mapping(target = "archiveCustomerId", ignore = true) // généré par la DB
    @Mapping(target = "originalCustomerId", source = "customerId")
    @Mapping(target = "archiveCreatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "accounts", source = "accounts")
    CustomerArchive customerArchiveDtoToCustomerArchive(CustomerArchiveDTO customerArchiveDTO);

    @InheritInverseConfiguration
    @Mapping(target = "accounts", source = "accounts")
    CustomerArchiveDTO customerArchiveToCustomerArchiveDto(CustomerArchive customerArchive);

    // ===== ACCOUNT =====
    @Mapping(target = "archiveAccountId", ignore = true)
    @Mapping(target = "originalAccountId", source = "accountId")
    @Mapping(target = "customerArchive", ignore = true)
    @Mapping(target = "operations", source = "operations")
    AccountArchive accountArchiveDtoToAccountArchive(AccountArchiveDTO accountArchiveDTO);

    @InheritInverseConfiguration
    @Mapping(target = "accountId", source = "originalAccountId")
    AccountArchiveDTO accountArchiveToAccountArchiveDto(AccountArchive accountArchive);

    // ===== OPERATION =====

    @Mapping(target = "originalOperationId", source = "operationId")
    @Mapping(target = "archiveOperationId", ignore = true)
    @Mapping(target = "operationAmount", source = "operationAmount")
    @Mapping(target = "accountArchive", ignore = true)
    OperationArchive operationArchiveDtoToOperationArchive(OperationArchiveDTO dto);

    @InheritInverseConfiguration
    @Mapping(target = "operationId", source = "originalOperationId")
    OperationArchiveDTO operationArchiveToOperationArchiveDto(OperationArchive entity);

    // ===== BIDIRECTIONAL WIRING =====
    @AfterMapping
    default void linkBidirectionalRelationships(@MappingTarget CustomerArchive customerArchive) {
        customerArchive.getAccounts().forEach(account -> {
            account.setCustomerArchive(customerArchive);
            account.getOperations().forEach(op -> op.setAccountArchive(account));
        });
    }

    // ===== COLLECTIONS =====
    List<AccountArchive> accountArchiveDtosToEntities(List<AccountArchiveDTO> dtos);
    List<AccountArchiveDTO> accountArchivesToDtos(List<AccountArchive> entities);

    List<OperationArchive> operationArchiveDtosToEntities(List<OperationArchiveDTO> dtos);
    List<OperationArchiveDTO> operationArchivesToDtos(List<OperationArchive> entities);
}
