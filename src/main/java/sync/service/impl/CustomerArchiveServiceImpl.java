package sync.service.impl;

import org.springframework.transaction.annotation.Transactional;
import sync.dtos.CustomerArchiveDTO;
import sync.entities.CustomerArchive;
import sync.mappers.CustomerArchiveMapper;
import sync.repositories.CustomerArchiveRepository;
import sync.service.CustomerArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerArchiveServiceImpl implements CustomerArchiveService {

    private final CustomerArchiveRepository customerArchiveRepository;
    private final CustomerArchiveMapper customerArchiveMapper;

    @Transactional
    @Override
    public void saveArchivedCustomer(CustomerArchiveDTO customerArchiveDTO) {
        log.info("Start of customer archiving: CustomerId: {}, & Email ({})",
                customerArchiveDTO.getCustomerId(), customerArchiveDTO.getEmail());

        // Convert  DTO to -> Entity
        CustomerArchive customerArchive =
                customerArchiveMapper.customerArchiveDtoToCustomerArchive(customerArchiveDTO);

        if (customerArchive != null && customerArchive.getAccounts() != null) {
            customerArchive.getAccounts().forEach(account -> {
                account.setCustomerArchive(customerArchive);

                if (account.getOperations() != null) {
                    account.getOperations().forEach(operationArchive ->
                            operationArchive.setAccountArchive(account));
                }
            });

            if (customerArchive.getArchiveCreatedAt() == null) {
                customerArchive.setArchiveCreatedAt(LocalDateTime.now());
            }

            // Save to DB
            customerArchiveRepository.save(customerArchive);
        }
        log.info(" Archiving completed for customerID: {} with {} accounts.",
                customerArchiveDTO.getCustomerId(),
                (customerArchiveDTO.getAccounts() != null ? customerArchiveDTO.getAccounts().size() : 0));
    }
}
