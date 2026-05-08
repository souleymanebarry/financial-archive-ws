package sync.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import sync.dtos.CustomerArchiveDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/archives")
public interface CustomerArchiveController {

    @PreAuthorize("hasAuthority('SCOPE_archive:write')")
    @PostMapping("/customers")
    ResponseEntity<Void> archiveCustomer(@RequestBody @Valid CustomerArchiveDTO customerArchiveDTO);

}
