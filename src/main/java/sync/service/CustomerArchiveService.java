package sync.service;

import sync.dtos.CustomerArchiveDTO;

public interface CustomerArchiveService {

    void saveArchivedCustomer(CustomerArchiveDTO customerArchiveDTO);
}
