package sync.controller.impl;

import org.springframework.http.HttpStatus;
import sync.controller.CustomerArchiveController;
import sync.dtos.CustomerArchiveDTO;
import sync.service.CustomerArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
public class CustomerArchiveControllerImpl implements CustomerArchiveController {

    private final CustomerArchiveService archiveService;

    @Override
    public ResponseEntity<Void> archiveCustomer(CustomerArchiveDTO customerArchiveDTO) {
        archiveService.saveArchivedCustomer(customerArchiveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
